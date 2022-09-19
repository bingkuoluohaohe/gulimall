package com.jnu.gulimall.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnu.common.exception.PhoneException;
import com.jnu.common.exception.UsernameException;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.R;
import com.jnu.gulimall.vo.GiteeSocialUser;
import com.jnu.gulimall.vo.MemberUserLoginVo;
import com.jnu.gulimall.vo.MemberUserRegisterVo;
import com.jnu.gulimall.entity.member.MemberEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 会员
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:10:58
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 用户登录
     */
    MemberEntity login(MemberUserLoginVo vo);

     R loginUser(MemberUserLoginVo vo);

    /**
     * 社交用户的登录
     */
    MemberEntity login(GiteeSocialUser socialUser) throws Exception;

    R oauthLoginUser(GiteeSocialUser socialUser) throws Exception;

    /**
     * 判断邮箱是否重复
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 判断用户名是否重复
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    void register(MemberUserRegisterVo vo);

    R registerUser(MemberUserRegisterVo vo);
}

