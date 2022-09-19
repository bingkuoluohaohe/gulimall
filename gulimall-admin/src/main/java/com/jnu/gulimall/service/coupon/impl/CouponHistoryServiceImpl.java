package com.jnu.gulimall.service.coupon.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.Query;
import com.jnu.gulimall.dao.coupon.CouponHistoryDao;
import com.jnu.gulimall.entity.coupon.CouponHistoryEntity;
import com.jnu.gulimall.service.coupon.CouponHistoryService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;


@Primary
@Service("couponHistoryService")
public class CouponHistoryServiceImpl extends ServiceImpl<CouponHistoryDao, CouponHistoryEntity> implements CouponHistoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponHistoryEntity> page = this.page(
                new Query<CouponHistoryEntity>().getPage(params),
                new QueryWrapper<CouponHistoryEntity>()
        );

        return new PageUtils(page);
    }

}