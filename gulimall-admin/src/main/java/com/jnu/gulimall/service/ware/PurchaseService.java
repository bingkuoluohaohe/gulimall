package com.jnu.gulimall.service.ware;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.utils.PageUtils;
import com.jnu.gulimall.entity.ware.PurchaseEntity;
import com.jnu.gulimall.vo.MergeVo;
import com.jnu.gulimall.vo.PurchaseDoneVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:38:25
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    @Transactional
    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);
}

