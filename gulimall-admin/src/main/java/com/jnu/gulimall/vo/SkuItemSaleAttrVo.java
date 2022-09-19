package com.jnu.gulimall.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author ych
 * @date 2022/09/25 22:46
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    /**
     * 每一个销售属性都有attrName，
     * 并且有多个attrValues
     */
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
