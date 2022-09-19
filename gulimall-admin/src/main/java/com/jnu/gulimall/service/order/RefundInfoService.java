package com.jnu.gulimall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.order.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:57
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

