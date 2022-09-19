package com.jnu.gulimall.controller.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jnu.common.constant.AuthServerConstant;
import com.jnu.common.constant.GiteeConstant;
import com.jnu.common.utils.HttpUtils;
import com.jnu.common.utils.R;
import com.jnu.gulimall.service.member.MemberService;
import com.jnu.gulimall.vo.GiteeSocialUser;
import com.jnu.gulimall.vo.MemberResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
public class OAuth2Controller {

    @Resource
    MemberService memberService;

    @GetMapping(value = "/oauth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session) throws Exception {

        //这个几个参数格式是强制性的 可以参考码云的官方api
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", GiteeConstant.clientId);
        params.put("redirect_uri", GiteeConstant.callback);
        params.put("client_secret", GiteeConstant.secret);

        //1、根据用户授权返回的code换取access_token
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), params, new HashMap<>());
//2、处理
        if (response.getStatusLine().getStatusCode() == 200) {
            //获取到了access_token
            String json = EntityUtils.toString(response.getEntity());// 获取到json串
            //String json = JSON.toJSONString(response.getEntity());
            GiteeSocialUser socialUser = JSON.parseObject(json, GiteeSocialUser.class);

            //知道了哪个社交用户
            //1）、当前用户如果是第一次进网站，自动注册进来（为当前社交用户生成一个会员信息，以后这个社交账号就对应指定的会员）
            //登录或者注册这个社交用户
            System.out.println("登录后用code换取的token值：" + socialUser.getAccessToken());
            //调用远程服务
            R oauthLogin = memberService.oauthLoginUser(socialUser);
            if (oauthLogin.getCode() == 0) {
                MemberResponseVo data = oauthLogin.getData("data", new TypeReference<MemberResponseVo>() {
                });
                log.info("登录成功：用户信息：\n{}", data.toString());

                //1、第一次使用session，命令浏览器保存卡号，JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个网站的cookie
                //TODO 1、默认发的令牌。当前域（解决子域session共享问题）
                //TODO 2、使用JSON的序列化方式来序列化对象到Redis中
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);

                //2、登录成功跳回首页
                return "redirect:http://codekenan.icu/shop-vue";
            } else {
                return "redirect:http://codekenan.icu/auth/login.html";
            }
        } else {
            return "redirect:http://codekenan.icu/auth/login.html";
        }
    }

}
