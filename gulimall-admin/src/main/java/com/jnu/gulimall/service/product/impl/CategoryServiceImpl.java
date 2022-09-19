package com.jnu.gulimall.service.product.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.Query;
import com.jnu.gulimall.dao.product.CategoryDao;
import com.jnu.gulimall.entity.product.CategoryEntity;
import com.jnu.gulimall.service.product.CategoryBrandRelationService;
import com.jnu.gulimall.service.product.CategoryService;
import com.jnu.gulimall.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : [游成鹤]
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * @return 三级分类菜单
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = this.list();
        //2、组装成父子的树形结构
        //2.1）、找到所有的一级分类
        return entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .peek(menu -> menu.setChildren(getChildrens(menu, entities)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root 父菜单
     * @param all  所有分类数据
     * @return 子分类
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
                //1、找到子菜单
                .peek(categoryEntity -> categoryEntity.setChildren(getChildrens(categoryEntity, all)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * 批量删除
     *
     * @param ids 分类id数组
     */
    @CacheEvict(value = "product:category",key = "'getLevel1Categorys'")
    @Override
    public void removeMenuByids(Long[] ids) {
        // TODO 1.检查当前删除的菜单，是否被别的地方引用
        if (ids == null || ids.length == 0) {
            throw new RuntimeException("主键值为空");
        } else {
            categoryDao.deleteByIds(ids);
        }
    }

    @Override
    public Long[] findCatelogPath(Long id) {
        List<Long> paths = new ArrayList<>();
        findParentPath(id, paths);
        return paths.toArray(new Long[0]);
    }

    private void findParentPath(Long categoryId, List<Long> paths) {
        CategoryEntity category = this.getById(categoryId);
        paths.add(0, categoryId);
        if (category.getParentCid() != 0) {
            findParentPath(category.getParentCid(), paths);
        }
    }

    /**
     * 级联更新所有关联数据
     */
    @Override
    @CacheEvict(value = "product:category",key = "'getLevel1Categorys'")
    @Transactional
    public void updateCascade(CategoryEntity category) {
        categoryDao.updateByCatId(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 更新排序
     */
    @Override
    public void updateSort(CategoryEntity[] category) {
        updateBatchById(Arrays.asList(category));
        categoryDao.updateSort(category);
    }

    /**
     * 查询一级分类
     */
    @Cacheable(value = "product:category", key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return categoryDao.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 1）springboot2.0以后默认使用lettuce作为操作redis的客户端。他使用netty进行网络通信
     * 2）lettuce的bug导致netty堆外内存溢出 -Xmx1024m；netty如果没有指定堆外内存，默认使用-Xmx1024m，跟jvm设置的一样【迟早会出异常】
     * 可以通过-Dio.netty.maxDirectMemory进行设置【仍然会异常】
     * 解决方案：不能使用-Dio.netty.maxDirectMemory
     * 1）升级lettuce客户端；【2.3.2已解决】【lettuce使用netty吞吐量很大】
     * 2）切换使用jedis客户端【这里学习一下如何使用jedis，但是最后不选用】
     */
    //TODO 产生堆外内存溢出：OutOfDirectMemoryError
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        //1.加入缓存逻辑
        String catelogJSON = stringRedisTemplate.opsForValue().get("product:catelogJSON");
        if (StringUtils.isEmpty(catelogJSON)) {
            //2.缓存中没有,查询数据库
            System.out.println("缓存未命中.....查询数据库");
            return getCatalogJsonFromDbWithRedisLock();
        }
        System.out.println("缓存命中.....直接返回");
        return JSON.parseObject(catelogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
    }

    /**
     * redisson加锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisson() {
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();
        System.out.println("Redisson 获取分布式锁成功....");
        Map<String, List<Catalog2Vo>> dataFromDB = null;
        try {
            dataFromDB = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDB;
    }

    /**
     * 分布式锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        // 1、占本分布式锁。去redis占坑，同时设置过期时间
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            // 加锁成功....执行业务【内部会判断一次redis是否有值】
            System.out.println("获取分布式锁成功....");
            Map<String, List<Catalog2Vo>> dataFromDB = null;
            try {
                dataFromDB = getDataFromDb();
            } finally {
                // 2、查询UUID是否是自己，是自己的lock就删除
                // 查询+删除 必须是原子操作：lua脚本解锁
                String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call('del',KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                // 删除锁
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(luaScript, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            return dataFromDB;
        } else {
            System.out.println("获取分布式锁失败....等待重试...");
            // 加锁失败....重试
            // 休眠100ms重试
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();// 自旋的方式
        }
    }

    public Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJSON = stringRedisTemplate.opsForValue().get("product:catelogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
        }

        System.out.println("查询了数据库.....");

        // 一次性获取所有 数据
        List<CategoryEntity> selectList = categoryDao.selectList(null);
        System.out.println("调用了 getCatalogJson  查询了数据库........【三级分类】");
        // 1）、所有1级分类
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        // 2）、封装数据
        Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
            // 查到当前1级分类的2级分类
            List<CategoryEntity> category2level = getParentCid(selectList, level1.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (category2level != null) {
                catalog2Vos = category2level.stream().map(level12 -> {
                    // 查询当前2级分类的3级分类
                    List<CategoryEntity> category3level = getParentCid(selectList, level12.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
                    if (category3level != null) {
                        catalog3Vos = category3level.stream().map(level13 ->
                                new Catalog2Vo.Catalog3Vo(level12.getCatId().toString(), level13.getCatId().toString(), level13.getName())).collect(Collectors.toList());
                    }
                    return new Catalog2Vo(level1.getCatId().toString(), catalog3Vos, level12.getCatId().toString(), level12.getName());
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));

        //3.查到的数据再放入缓存,将对象转为json放在缓存中
        String s = JSON.toJSONString(collect);
        stringRedisTemplate.opsForValue().set("product:catelogJSON", s, 1, TimeUnit.HOURS);
        return collect;
    }

    /**
     * 查询出父ID为 parent_cid的List集合
     */
    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parent_cid) {
        return selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
    }

}

