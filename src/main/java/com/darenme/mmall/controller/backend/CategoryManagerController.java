package com.darenme.mmall.controller.backend;

import com.darenme.mmall.common.Const;
import com.darenme.mmall.common.ResponseCode;
import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.pojo.User;
import com.darenme.mmall.service.ICategoryService;
import com.darenme.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by darenme
 * date: 2018/8/11
 * time: 14:20
 */

@Controller
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，请登录管理员");

        }

        // 校验是否是管理员
        if (iUserService.checkAdmin(user).isSuccess()) {
            // 是管理员
            // 增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，请登录管理员");
        }
        // 校验是否是管理员
        if (iUserService.checkAdmin(user).isSuccess()) {
            // 是更新CategoryName
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，请登录管理员");
        }

        // 校验是否是管理员
        if (iUserService.checkAdmin(user).isSuccess()) {

            // 查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCatogory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，请登录管理员");
        }

        // 校验是否是管理员
        if (iUserService.checkAdmin(user).isSuccess()) {

            // 查询子节点的id和递归子节点的id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
