package com.botmasterzzz.auth.service;

import com.botmasterzzz.auth.entity.User;
import com.botmasterzzz.auth.entity.UserAuthEntity;

import javax.servlet.http.HttpServletRequest;

public interface AsyncLoggerService {

    void userAdd(User user, HttpServletRequest request);

    void userAuthEntityAdd(UserAuthEntity userAuthEntity);

}
