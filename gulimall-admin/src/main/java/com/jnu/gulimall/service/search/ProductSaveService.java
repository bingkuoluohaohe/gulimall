package com.jnu.gulimall.service.search;

import com.jnu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author ych
 * @date 2022/10/26 13:50
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
