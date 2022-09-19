package com.jnu.gulimall.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.product.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-06 15:47:53
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfoDesc(SpuInfoDescEntity descEntity);
}

