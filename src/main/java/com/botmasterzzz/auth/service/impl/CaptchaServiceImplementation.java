package com.botmasterzzz.auth.service.impl;

import com.botmasterzzz.auth.exception.InvalidReCaptchaException;
import com.botmasterzzz.auth.model.GoogleResponse;
import com.botmasterzzz.auth.service.CaptchaService;
import com.botmasterzzz.auth.service.ReCaptchaAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.regex.Pattern;

@Service
public class CaptchaServiceImplementation implements CaptchaService {

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    private final static String GOOGLE_CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    @Value("${google.recaptcha.key.site}")
    private String secretSiteKey;

    @Value("${google.recaptcha.key.secret}")
    private String secretKeySecret;

    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;


    @Override
    public void processResponse(String response, String clientIp) {
        if (!responseSanityCheck(response)) {
            throw new InvalidReCaptchaException("Ошибка в проверки возвращаемой строки в капча");
        }

        URI verifyUri = URI.create(String.format(
                GOOGLE_CAPTCHA_URL,
                secretKeySecret, response, clientIp));

        GoogleResponse googleResponse = new RestTemplate().getForObject(verifyUri, GoogleResponse.class);

        if (!googleResponse.isSuccess()) {
            if (googleResponse.hasClientError()) {
                reCaptchaAttemptService.reCaptchaFailed(clientIp);
            }
            throw new InvalidReCaptchaException("Неудачная проверка строки в капча");
        }
        reCaptchaAttemptService.reCaptchaSucceeded(clientIp);
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
