package com.jnu.gulimall.controller.coupon;

import com.jnu.common.utils.PageUtils;
import com.jnu.common.utils.R;
import com.jnu.gulimall.entity.coupon.HomeAdvEntity;
import com.jnu.gulimall.service.coupon.HomeAdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 首页轮播广告
 *
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 19:48:58
 */
@RestController
@RequestMapping("coupon/homeadv")
public class HomeAdvController {
    @Autowired
    private HomeAdvService homeAdvService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = homeAdvService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        HomeAdvEntity homeAdv = homeAdvService.getById(id);

        return R.ok().put("homeAdv", homeAdv);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody HomeAdvEntity homeAdv) {
        homeAdvService.save(homeAdv);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody HomeAdvEntity homeAdv) {
        homeAdvService.updateById(homeAdv);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        homeAdvService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
