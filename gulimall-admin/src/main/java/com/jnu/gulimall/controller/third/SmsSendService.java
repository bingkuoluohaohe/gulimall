package com.jnu.gulimall.controller.third;

import com.jnu.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author ych
 * @date 2022/09/29 18:28
 */
@Service
public class SmsSendService {

    @Resource
    private SmsComponent smsComponent;

    public R sendCode(String phone, String code) {
        //发送验证码
        boolean b = smsComponent.sendCode(phone, code);
        if (b) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}
