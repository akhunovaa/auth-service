package com.botmasterzzz.auth.security.oauth2.user;

import com.botmasterzzz.auth.exception.OAuth2AuthenticationProcessingException;
import com.botmasterzzz.auth.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.github.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        }else if (registrationId.equalsIgnoreCase(AuthProvider.vk.toString())) {
            return new VkOAuth2UserInfo(attributes);
        }else if (registrationId.equalsIgnoreCase(AuthProvider.yandex.toString())) {
            return new YandexOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.battlenet.toString())) {
            return new BattleNetOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
