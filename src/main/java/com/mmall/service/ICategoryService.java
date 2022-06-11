package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICategoryService {
    public ServerResponse addCategory(String categoryName, Integer parentId);
    public ServerResponse setCategoryName(Integer categoryId,String categoryName);
    public ServerResponse getChildrenParallelCategory(Integer categoryId);
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
