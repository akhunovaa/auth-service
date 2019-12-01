package com.botmasterzzz.auth.controller;

import com.botmasterzzz.auth.exception.ResourceNotFoundException;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.repository.UserDao;
import com.botmasterzzz.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @PreAuthorize("authenticated")
    @RequestMapping(value = "/user/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getCurrentUser() {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) usernamePasswordAuthenticationToken.getPrincipal();
        long userId = userPrincipal.getId();
        return userDao.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

}
