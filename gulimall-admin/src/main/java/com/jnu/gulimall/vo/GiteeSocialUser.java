package com.jnu.gulimall.vo;

import lombok.Data;

/**
 * @author zr
 * @date 2021/12/8 17:22
 */
@Data
public class GiteeSocialUser {
    private String accessToken;

    private String tokenType;

    private Long expiresIn;

    private String refreshToken;

    private String scope;

    private String createdAt;

}
