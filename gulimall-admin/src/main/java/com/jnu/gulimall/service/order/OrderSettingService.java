package com.jnu.gulimall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.order.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:58
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

