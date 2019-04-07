package com.botmasterzzz.auth.controller;

import com.botmasterzzz.auth.exception.BadRequestException;
import com.botmasterzzz.auth.model.AuthProvider;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.payload.*;
import com.botmasterzzz.auth.repository.UserRepository;
import com.botmasterzzz.auth.security.TokenProvider;
import com.botmasterzzz.auth.service.CaptchaService;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;


    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
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
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
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
