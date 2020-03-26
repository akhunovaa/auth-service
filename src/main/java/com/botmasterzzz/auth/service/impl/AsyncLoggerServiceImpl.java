package com.botmasterzzz.auth.service.impl;

import com.botmasterzzz.auth.entity.User;
import com.botmasterzzz.auth.entity.UserAuthEntity;
import com.botmasterzzz.auth.repository.UserDao;
import com.botmasterzzz.auth.service.AsyncLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@EnableAsync
public class AsyncLoggerServiceImpl implements AsyncLoggerService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncLoggerServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public void userAdd(User user, HttpServletRequest request) {

    }

    @Async
    @Override
    public void userAuthEntityAdd(UserAuthEntity userAuthEntity) {

        userAuthEntity.setId(userDao.userAuthEntityAdd(userAuthEntity));

        logger.info("\n" +
                "Id \t" + userAuthEntity.getId() + "\n" +
                "User Agent \t" + userAuthEntity.getUserAgent() + "\n" +
                "Operating System\t" + userAuthEntity.getClientOs() + "\n" +
                "Browser Name\t" + userAuthEntity.getClientBrowser() + "\n" +
                "IP Address\t" + userAuthEntity.getIpAddress() + "\n" +
                "Full URL\t" + userAuthEntity.getFullUrl() + "\n" +
                "Note\t" + userAuthEntity.getNote() + "\n" +
                "Token\t" + userAuthEntity.getToken() + "\n" +
                "Referrer\t" + userAuthEntity.getReferer());
    }
}
