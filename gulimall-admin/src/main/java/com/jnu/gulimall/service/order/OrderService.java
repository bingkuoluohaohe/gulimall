package com.jnu.gulimall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.order.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:59
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

