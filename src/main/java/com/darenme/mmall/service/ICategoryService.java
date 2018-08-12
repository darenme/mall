package com.darenme.mmall.service;

import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.pojo.Category;

import java.util.List;

/**
 * Created by darenme
 * date: 2018/8/11
 * time: 19:22
 */

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, int parentId);
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
