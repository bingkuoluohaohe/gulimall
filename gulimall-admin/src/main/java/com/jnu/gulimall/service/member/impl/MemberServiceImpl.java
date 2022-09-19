package com.jnu.gulimall.service.member.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jnu.common.exception.BizCodeEnume;
import com.jnu.common.exception.PhoneException;
import com.jnu.common.exception.UsernameException;
import com.jnu.common.utils.HttpUtils;
import com.jnu.common.utils.R;
import com.jnu.gulimall.dao.member.MemberDao;
import com.jnu.gulimall.dao.member.MemberLevelDao;
import com.jnu.gulimall.entity.member.MemberEntity;
import com.jnu.gulimall.entity.member.MemberLevelEntity;
import com.jnu.gulimall.service.member.MemberService;
import com.jnu.gulimall.vo.GiteeSocialUser;
import com.jnu.gulimall.vo.MemberUserLoginVo;
import com.jnu.gulimall.vo.MemberUserRegisterVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberUserRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();

        //设置默认等级【普通会员】
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //设置其它的默认信息
        //检查用户名和手机号是否唯一。感知异常，异常机制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        memberEntity.setNickname(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        //密码进行MD5加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setGender(0);
        memberEntity.setCreateTime(new Date());

        //保存数据
        baseMapper.insert(memberEntity);
    }

    public R registerUser(MemberUserRegisterVo vo) {
        try {
            register(vo);
        } catch (PhoneException e) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameException e) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {
        Integer phoneCount = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (phoneCount > 0) {
            throw new PhoneException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UsernameException {
        Integer usernameCount = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (usernameCount > 0) {
            throw new UsernameException();
        }
    }

    /**
     * 本地登录
     */
    @Override
    public MemberEntity login(MemberUserLoginVo vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //1、去数据库查询 SELECT * FROM ums_member WHERE username = ? OR mobile = ?
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct).or().eq("mobile", loginacct));

        if (memberEntity == null) {
            //登录失败
            return null;
        } else {
            //获取到数据库里的password
            String password1 = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //进行密码匹配
            boolean matches = passwordEncoder.matches(password, password1);
            if (matches) {
                //登录成功
                System.out.println("登录成功");
                return memberEntity;
            }
        }
        return null;
    }

    /**
     * 登录接口
     */
    public R loginUser(MemberUserLoginVo vo) {

        MemberEntity memberEntity = login(vo);

        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    @Override
    public MemberEntity login(GiteeSocialUser socialUser) throws Exception {
        Map<String, String> query = new HashMap<>();
        query.put("access_token", socialUser.getAccessToken());
        HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<String, String>(), query);
        String json = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = JSON.parseObject(json);
        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String gender = jsonObject.getString("gender");
        String profileImageUrl = jsonObject.getString("avatar_url");
        //具有登录和注册逻辑
        String uid = id;

        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if (memberEntity != null) {
            //这个用户已经注册过
            //更新用户的访问令牌的时间和access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
//            update.setAccessToken(socialUser.getAccessToken());
//            update.setExpiresIn(socialUser.getExpiresIn());
            baseMapper.updateById(update);

//            memberEntity.setAccessToken(socialUser.getAccessToken());
//            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            // 远程调用，不影响结果
            try {
//                Map<String, String> query = new HashMap<>();
//                query.put("access_token", socialUser.getAccessToken());
//                HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<String, String>(), query);

                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
//                    String gender = jsonObject.getString("gender");
                    register.setUsername(name);
                    register.setNickname(name);
                    register.setCreateTime(new Date());
                    register.setGender("m".equals(gender) ? 1 : 0);
                    register.setHeader(profileImageUrl);
                }
            }catch (Exception e){}
            register.setCreateTime(new Date());
//            register.setSocialUid(uid);
//            register.setAccessToken(socialUser.getAccessToken());
//            register.setExpiresIn(socialUser.getExpiresIn());

            //把用户信息插入到数据库中
            baseMapper.insert(register);
            return register;
        }
    }

    public R oauthLoginUser(GiteeSocialUser socialUser) throws Exception {

        MemberEntity memberEntity = login(socialUser);

        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
        }
    }

}