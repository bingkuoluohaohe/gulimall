package com.jnu.gulimall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.order.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:57
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

