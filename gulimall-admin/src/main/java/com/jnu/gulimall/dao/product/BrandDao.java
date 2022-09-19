package com.jnu.gulimall.dao.product;

import com.jnu.gulimall.entity.product.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌
 * 
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-06 15:47:54
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    void updateStatus(BrandEntity brand);
}
