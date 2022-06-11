package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICartService {
    public ServerResponse add(Integer userId, Integer productId, Integer count);
    public ServerResponse update(Integer userId, Integer productId, Integer count);
    // public ServerResponse delete(Integer userId, String productIds);
}
