package com.jnu.gulimall.service.coupon.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnu.common.to.SkuReductionTo;
import com.jnu.common.to.SpuBoundTo;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.Query;
import com.jnu.common.utils.R;
import com.jnu.gulimall.dao.coupon.SpuBoundsDao;
import com.jnu.gulimall.entity.coupon.SpuBoundsEntity;
import com.jnu.gulimall.service.coupon.SpuBoundsService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuBoundsEntity> page = this.page(
                new Query<SpuBoundsEntity>().getPage(params),
                new QueryWrapper<SpuBoundsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R saveSpuBounds(SpuBoundTo spuBoundTo) {
        SpuBoundsEntity spuBoundsEntity = new SpuBoundsEntity();
        spuBoundsEntity.setSpuId(spuBoundTo.getSpuId());
        spuBoundsEntity.setBuyBounds(spuBoundsEntity.getBuyBounds());
        spuBoundsEntity.setGrowBounds(spuBoundsEntity.getGrowBounds());
        this.save(spuBoundsEntity);
        return R.ok();
    }
}