package com.jnu.common.exception;

/**
 * @author zr
 * @date 2021/11/30 11:02
 */
public class UsernameException extends RuntimeException {
    public UsernameException() {
        super("存在相同的用户名");
    }
}
