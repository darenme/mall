package com.darenme.mmall.service;

import com.darenme.mmall.common.ServerResponse;
import com.darenme.mmall.pojo.User;

/**
 * Created by darenme
 * date: 2018/8/5
 * time: 22:45
 */

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str, String type);
    ServerResponse selectQuestion(String username);
    ServerResponse<String> checkAnswer(String username, String question, String answer);
    ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken);
    ServerResponse<String> restPassword(String passwordOld, String passwordNew, User user);
    ServerResponse<User> updateInformation(User user);
    ServerResponse<User> getInformation(Integer userId);
    ServerResponse checkAdmin(User user);
}
