package com.jnu.gulimall.dao.coupon;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnu.gulimall.entity.coupon.CouponSpuRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 19:48:58
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {

}
