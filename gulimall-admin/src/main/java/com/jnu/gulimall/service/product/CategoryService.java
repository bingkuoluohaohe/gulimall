package com.jnu.gulimall.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.product.CategoryEntity;
import com.jnu.gulimall.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-06 15:47:52
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByids(Long[] ids);

    Long[] findCatelogPath(Long id);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2Vo>> getCatalogJson();

    void updateSort(CategoryEntity[] category);
}

