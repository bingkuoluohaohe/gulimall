package com.jnu.gulimall.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.product.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-06 15:47:53
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

