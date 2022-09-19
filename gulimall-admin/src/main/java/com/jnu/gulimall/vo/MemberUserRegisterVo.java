package com.jnu.gulimall.vo;
/**
 * @author zr
 * @date 2021/11/30 11:01
 */

import lombok.Data;

/**
 * 会员注册Vo
 */
@Data
public class MemberUserRegisterVo {
    private String userName;
    private String password;
    private String phone;
}
