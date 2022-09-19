package com.jnu.gulimall.dao.order;

import com.jnu.gulimall.entity.order.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:32:57
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
