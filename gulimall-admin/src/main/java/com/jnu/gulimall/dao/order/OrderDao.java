package com.jnu.gulimall.dao.order;

import com.jnu.gulimall.entity.order.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:59
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
