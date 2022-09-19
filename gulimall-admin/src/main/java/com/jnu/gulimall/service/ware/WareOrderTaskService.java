package com.jnu.gulimall.service.ware;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.ware.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:38:25
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

