package com.botmasterzzz.auth.controller;

import com.botmasterzzz.auth.exception.ResourceNotFoundException;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.repository.UserRepository;
import com.botmasterzzz.auth.security.CurrentUser;
import com.botmasterzzz.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EXTERNAL_ADMIN', 'ROLE_EXTERNAL_MANAGER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

}
