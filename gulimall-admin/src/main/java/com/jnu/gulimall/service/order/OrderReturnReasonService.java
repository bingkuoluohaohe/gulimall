package com.jnu.gulimall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.order.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:58
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

