package com.jnu.gulimall.service.search;


import com.jnu.gulimall.vo.SearchParam;
import com.jnu.gulimall.vo.SearchResult;

/**
 * @author ych
 * @date 2022/09/17 18:06
 */
public interface MallSearchService {
    SearchResult search(SearchParam param);
}
