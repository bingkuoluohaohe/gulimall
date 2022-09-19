package com.jnu.gulimall.dao.product;

import com.jnu.gulimall.entity.product.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品三级分类
 * 
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-06 15:47:52
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    int deleteByIds(@Param("ids") Long[] ids);

    int updateByCatId(CategoryEntity category);

    void updateSort(@Param("categorys") CategoryEntity[] category);
}
