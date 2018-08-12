package com.darenme.mmall.service;

import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.vo.CartVo;

/**
 * Created by darenme
 * date: 2018/8/12
 * time: 16:12
 */

public interface ICartService {
    ServerResponse add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> list (Integer userId);

    public ServerResponse<CartVo> selectOrUnSelect (Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
