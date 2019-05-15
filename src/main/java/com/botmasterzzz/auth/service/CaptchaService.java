package com.botmasterzzz.auth.service;

public interface CaptchaService {

    void processResponse(String response, String clientIp);

}
