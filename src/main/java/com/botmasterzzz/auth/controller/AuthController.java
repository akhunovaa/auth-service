package com.botmasterzzz.auth.controller;

import com.botmasterzzz.auth.exception.BadRequestException;
import com.botmasterzzz.auth.model.AuthProvider;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserAuthEntity;
import com.botmasterzzz.auth.model.UserRole;
import com.botmasterzzz.auth.payload.*;
import com.botmasterzzz.auth.repository.UserAuthRepository;
import com.botmasterzzz.auth.repository.UserRepository;
import com.botmasterzzz.auth.security.TokenProvider;
import com.botmasterzzz.auth.security.UserPrincipal;
import com.botmasterzzz.auth.service.CaptchaService;
import com.botmasterzzz.auth.util.ClientInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Date;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
        );

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
        UserAuthEntity userAuthEntityResult = userAuthRepository.save(userAuthEntity);

        logger.info("\n" +
                "Id \t" + userAuthEntityResult.getId() + "\n" +
                "User Agent \t" + userAuthEntityResult.getUserAgent() + "\n" +
                "Operating System\t" + userAuthEntityResult.getClientOs() + "\n" +
                "Browser Name\t" + userAuthEntityResult.getClientBrowser() + "\n" +
                "IP Address\t" + userAuthEntityResult.getIpAddress() + "\n" +
                "Full URL\t" + userAuthEntityResult.getFullUrl() + "\n" +
                "Note\t" + userAuthEntityResult.getNote() + "\n" +
                "Token\t" + userAuthEntityResult.getToken() + "\n" +
                "Referrer\t" + userAuthEntityResult.getReferer());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String response = signUpRequest.getCaptchaToken();
        captchaService.processResponse(response, ipAddress);

        if(userRepository.existsByLogin(signUpRequest.getLogin())) {
            throw new BadRequestException("Данный логин уже занят");
        }

        // Creating user's account
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
        User result = userRepository.save(user);

        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUser(user);
        userAuthEntity.setClientBrowser(ClientInfoUtil.getClientBrowser(request));
        userAuthEntity.setClientOs(ClientInfoUtil.getClientOS(request));
        userAuthEntity.setFullUrl(ClientInfoUtil.getFullURL(request));
        userAuthEntity.setIpAddress(ClientInfoUtil.getClientIpAddr(request));
        userAuthEntity.setReferer(ClientInfoUtil.getReferer(request));
        userAuthEntity.setUserAgent(ClientInfoUtil.getUserAgent(request));
        userAuthEntity.setNote("SIGNUP");
        UserAuthEntity userAuthEntityResult = userAuthRepository.save(userAuthEntity);

        logger.info("\n" +
                "Id \t" + userAuthEntityResult.getId() + "\n" +
                "User Agent \t" + userAuthEntityResult.getUserAgent() + "\n" +
                "Operating System\t" + userAuthEntityResult.getClientOs() + "\n" +
                "Browser Name\t" + userAuthEntityResult.getClientBrowser() + "\n" +
                "IP Address\t" + userAuthEntityResult.getIpAddress() + "\n" +
                "Full URL\t" + userAuthEntityResult.getFullUrl() + "\n" +
                "Note\t" + userAuthEntityResult.getNote() + "\n" +
                "Referrer\t" + userAuthEntityResult.getReferer());
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Пользователь успешно зарегистрирован"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/check")
    public TokenChecker tokenCheck(@Valid @RequestParam String token) {
        TokenChecker tokenChecker = new TokenChecker();
        Long id = tokenProvider.getUserIdFromToken(token);
        boolean validation = tokenProvider.validateToken(token);
        Date exp = tokenProvider.getExpFromToken(token);
        tokenChecker.setId(id);
        tokenChecker.setValidation(validation);
        if (validation){
            tokenChecker.setStatus("ok");
            tokenChecker.setExp(exp);
            tokenChecker.setMessage("good");
        }else{
            tokenChecker.setStatus("false");
            tokenChecker.setMessage("Some error there");
        }
        return tokenChecker;
    }

}
