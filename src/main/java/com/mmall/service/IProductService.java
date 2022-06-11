package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

public interface IProductService {
    public ServerResponse saveOrUpdateProduct(Product product);

    public ServerResponse setSaleStatus(Integer productId, Integer productStatus);

    public ServerResponse manageProductDetail(Integer productId);

    public ServerResponse getProductList(int pageNum, int pageSize);

    public ServerResponse searchProduct(String productName, Integer productId,int pageNum,int pageSize);
}
