package com.darenme.mmall.service;

import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.pojo.Product;
import com.darenme.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

/**
 * Created by darenme
 * date: 2018/8/11
 * time: 21:50
 */

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse setSaleStatus(Integer productId, Integer status);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
