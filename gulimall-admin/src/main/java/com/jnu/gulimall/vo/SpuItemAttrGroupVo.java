package com.jnu.gulimall.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author ych
 * @date 2022/09/25 22:47
 */
@Data
@ToString
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
