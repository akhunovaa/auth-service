package com.botmasterzzz.auth.controller;

import com.botmasterzzz.auth.exception.InvalidLoginException;
import com.botmasterzzz.auth.model.UserAuthEntity;
import com.botmasterzzz.auth.provider.JwtTokenProvider;
import com.botmasterzzz.auth.model.AuthProvider;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserRole;
import com.botmasterzzz.auth.payload.*;
import com.botmasterzzz.auth.repository.UserDao;
import com.botmasterzzz.auth.security.UserPrincipal;
import com.botmasterzzz.auth.service.AsyncLoggerService;
import com.botmasterzzz.auth.service.CaptchaService;
import com.botmasterzzz.auth.util.ClientInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AsyncLoggerService asyncLoggerService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));
        }catch (BadCredentialsException badCredentialsException){
            throw new InvalidLoginException("Введен неверный логин или пароль!");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = new User();
        user.setId(userPrincipal.getId());
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUser(user);
        userAuthEntity.setClientBrowser(ClientInfoUtil.getClientBrowser(request));
        userAuthEntity.setClientOs(ClientInfoUtil.getClientOS(request));
        userAuthEntity.setFullUrl(ClientInfoUtil.getFullURL(request));
        userAuthEntity.setIpAddress(ClientInfoUtil.getClientIpAddr(request));
        userAuthEntity.setReferer(ClientInfoUtil.getReferer(request));
        userAuthEntity.setUserAgent(ClientInfoUtil.getUserAgent(request));
        userAuthEntity.setToken(token);
        userAuthEntity.setNote("LOGIN");
        asyncLoggerService.userAuthEntityAdd(userAuthEntity);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request){
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String response = signUpRequest.getCaptchaToken();
        captchaService.processResponse(response, ipAddress);

        if(userDao.findByLogin(signUpRequest.getLogin()).isPresent()) {
            throw new InvalidLoginException("Данный логин зарегистрирован в системе");
        }

        if(userDao.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new InvalidLoginException("Данный email зарегистрирован в системе");
        }

        User user = new User();
        UserRole userRole = new UserRole();
        userRole.setId(4L);
        user.setLogin(signUpRequest.getLogin());
        user.setPassword(signUpRequest.getPassword());
        user.setName(signUpRequest.getName());
        user.setSurname(signUpRequest.getSurname());
        user.setPatrName(signUpRequest.getPatrName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setProvider(AuthProvider.local);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserRole(userRole);
        user.setId(userDao.userAdd(user));
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUser(user);
        userAuthEntity.setClientBrowser(ClientInfoUtil.getClientBrowser(request));
        userAuthEntity.setClientOs(ClientInfoUtil.getClientOS(request));
        userAuthEntity.setFullUrl(ClientInfoUtil.getFullURL(request));
        userAuthEntity.setIpAddress(ClientInfoUtil.getClientIpAddr(request));
        userAuthEntity.setReferer(ClientInfoUtil.getReferer(request));
        userAuthEntity.setUserAgent(ClientInfoUtil.getUserAgent(request));
        userAuthEntity.setNote("SIGNUP");
        asyncLoggerService.userAuthEntityAdd(userAuthEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Пользователь успешно зарегистрирован"));
    }

}
