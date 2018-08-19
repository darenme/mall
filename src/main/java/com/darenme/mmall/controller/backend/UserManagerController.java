package com.darenme.mmall.controller.backend;

import com.darenme.mmall.common.Const;
import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.pojo.User;
import com.darenme.mmall.service.IUserService;
import com.darenme.mmall.util.CookieUtil;
import com.darenme.mmall.util.JsonUtil;
import com.darenme.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by darenme
 * date: 2018/8/10
 * time: 20:54
 */

@Controller
@RequestMapping("/manage/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()) {
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN) {
                // 登录的是管理员
                CookieUtil.writeLoginToken(httpServletResponse, session.getId());
                RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员无法登录");
            }
        }
        return response;
    }

}
