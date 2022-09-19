package com.jnu.gulimall.dao.order;

import com.jnu.gulimall.entity.order.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:58
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
