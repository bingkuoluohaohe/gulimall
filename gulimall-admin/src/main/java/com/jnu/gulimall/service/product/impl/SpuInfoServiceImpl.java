package com.jnu.gulimall.service.product.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnu.common.constant.ProductConstant;
import com.jnu.common.to.SkuReductionTo;
import com.jnu.common.to.SpuBoundTo;
import com.jnu.common.to.es.SkuEsModel;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.Query;
import com.jnu.common.utils.R;
import com.jnu.gulimall.dao.product.SpuInfoDao;
import com.jnu.gulimall.entity.product.*;
import com.jnu.gulimall.service.coupon.SkuFullReductionService;
import com.jnu.gulimall.service.coupon.SpuBoundsService;
import com.jnu.gulimall.service.product.*;
import com.jnu.gulimall.service.ware.WareSkuService;
import com.jnu.gulimall.service.search.impl.ElasticService;
import com.jnu.gulimall.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private SpuImagesService imagesService;

    @Resource
    private AttrService attrService;

    @Resource
    private ProductAttrValueService attrValueService;

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private SpuBoundsService spuBoundsService;

    @Resource
    private SkuFullReductionService skuFullReductionService;

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private WareSkuService wareSkuService;

    @Resource
    private ElasticService elasticService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * //TODO ??????????????????
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1?????????spu???????????? pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2?????????Spu??????????????? pms_spu_info_desc
        List<String> descript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", descript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3?????????spu???????????? pms_spu_images
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(), images);

        //4?????????spu???????????????;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);

        //5?????????spu??????????????????gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = spuBoundsService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("????????????spu??????????????????");
        }

        //5???????????????spu???????????????sku?????????
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1??????sku??????????????????pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //??????true???????????????false????????????
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2??????sku??????????????????pms_sku_image
                skuImagesService.saveBatch(imagesEntities);

                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3??????sku????????????????????????pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // //5.4??????sku??????????????????????????????gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    R r1 = skuFullReductionService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("????????????sku??????????????????");
                    }
                }
            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         */

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> idList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        List<ProductAttrValueEntity> baseAttrlistforspu = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrlistforspu.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds); ;
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> list = baseAttrlistforspu.stream().filter(attr -> idSet.contains(attr.getAttrId()))
                .map(attr -> {
                SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                BeanUtils.copyProperties(attr, attrs);
                return attrs;
        }).collect(Collectors.toList());

        // TODO ??????????????????????????????????????????????????????
        Map<Long, Boolean> booleanMap = null;
        try {
            R r = wareSkuService.hasStock(idList);
            TypeReference<List<SkuHasStockVo>> reference = new TypeReference<List<SkuHasStockVo>>() {};
            List<SkuHasStockVo> data = r.getData(reference);
            booleanMap = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        } catch (Exception e) {
            log.error("???????????????????????????{}", e);
        }

        Map<Long, Boolean> finalBooleanMap = booleanMap;
        List<SkuEsModel> upProducts = skuInfoEntities.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());

            if (finalBooleanMap == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalBooleanMap.get(sku.getSkuId()));
            }
            // ????????????
            skuEsModel.setHotScore(0L);

            BrandEntity brandEntity = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            skuEsModel.setAttrs(list);

            return skuEsModel;
        }).collect(Collectors.toList());

        // TODO 5?????????????????????es???????????????gulimall-search
        R r = elasticService.productStatusUp(upProducts);
        if (r.getCode() == 0) {
            // ??????????????????
            // TODO 6???????????????spu????????? ??????
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }
        // ??????????????????
        // TODO 7???????????????????????????????????????????????????
        //Feign????????????
        /*
          1???????????????????????????????????????json
               RequestTemplate template = buildTemplateFromArgs.create(argv);
          2?????????????????????????????????????????????????????????????????????
               excuteAndDecode(template)
          3?????????????????????????????????
               while(true){
                   try{
                       excuteAndDecode(template)
                   }catch() {
                       try{
                           // ????????????5?????????????????????
                           retryer.continueOrPropagate(e);
                        }catch() {
                           throw ex;
                        }
                       continue;
                   }
               }


         */
    }

}