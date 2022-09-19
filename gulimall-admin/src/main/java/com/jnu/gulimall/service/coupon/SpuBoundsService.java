package com.jnu.gulimall.service.coupon;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.to.SkuReductionTo;
import com.jnu.common.to.SpuBoundTo;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.R;
import com.jnu.gulimall.entity.coupon.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 19:48:57
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R saveSpuBounds(SpuBoundTo spuBoundTo);

}

