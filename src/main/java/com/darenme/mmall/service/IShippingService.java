package com.darenme.mmall.service;

import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

/**
 * Created by darenme
 * date: 2018/8/12
 * time: 21:07
 */

public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping);
    ServerResponse<String> del(Integer userId,Integer shippingId);
    ServerResponse update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
