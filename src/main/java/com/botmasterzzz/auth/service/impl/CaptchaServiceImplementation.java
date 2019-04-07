package com.botmasterzzz.auth.service.impl;

import com.botmasterzzz.auth.config.CaptchaSettings;
import com.botmasterzzz.auth.exception.InvalidReCaptchaException;
import com.botmasterzzz.auth.model.GoogleResponse;
import com.botmasterzzz.auth.service.CaptchaService;
import com.botmasterzzz.auth.service.ReCaptchaAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.regex.Pattern;

@Service
public class CaptchaServiceImplementation implements CaptchaService {

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private CaptchaSettings captchaSettings;

    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;


    @Override
    public void processResponse(String response, String clientIp) {
        if(!responseSanityCheck(response)) {
            throw new InvalidReCaptchaException("Response contains invalid characters");
        }

        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                captchaSettings.getSecret(), response, clientIp));

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

        if(!googleResponse.isSuccess()) {
            if(googleResponse.hasClientError()) {
                reCaptchaAttemptService.reCaptchaFailed(clientIp);
            }
            throw new InvalidReCaptchaException("reCaptcha was not successfully validated");
        }
        reCaptchaAttemptService.reCaptchaSucceeded(clientIp);
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
