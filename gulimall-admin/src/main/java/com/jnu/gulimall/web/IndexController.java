package com.jnu.gulimall.web;

import com.jnu.gulimall.entity.product.CategoryEntity;
import com.jnu.gulimall.service.product.CategoryService;
import com.jnu.gulimall.vo.Catalog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author ych
 * @date 2022/9/4 15:23
 */
@Controller
public class IndexController {
    @Resource
    private CategoryService categoryService;

    /**
     * 商城首页
     */
    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {
        // TODO 1、查出所有1级分类
        List<CategoryEntity> categoryEntitys = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntitys);
        return "index";
    }

    /**
     * 查出三级分类
     * 1级分类作为key，2级引用List
     */
    @ResponseBody
    @GetMapping("/product/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    @RequestMapping("/reg.html")
    public String reg(){
        return "reg";
    }
}
