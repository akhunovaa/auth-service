package com.botmasterzzz.auth.service;

import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserAuthEntity;

import javax.servlet.http.HttpServletRequest;

public interface AsyncLoggerService {

    void userAdd(User user, HttpServletRequest request);

    void userAuthEntityAdd(UserAuthEntity userAuthEntity);

}
