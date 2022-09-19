package com.jnu.gulimall.service.coupon;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.coupon.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 19:48:57
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

