package com.jnu.gulimall.dao.coupon;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnu.gulimall.entity.coupon.MemberPriceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 19:48:58
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {

}
