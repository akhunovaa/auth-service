package com.botmasterzzz.auth.provider;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum MyOAuth2Provider {

    VK {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"email", "photos"});
            builder.authorizationUri("https://oauth.vk.com/authorize?v=5.95&display=popup");
            builder.tokenUri("https://oauth.vk.com/access_token");
            builder.userInfoUri("https://api.vk.com/method/users.get?{user_id}&{access_token}&v=5.8&fields=photo_id, verified, sex, bdate, city, country, home_town, has_photo, photo_50, photo_100, photo_200_orig, photo_200, photo_400_orig, photo_max, photo_max_orig, online, domain, has_mobile, contacts, site, education, universities, schools, status, last_seen, followers_count, common_count, occupation, nickname, relatives, relation, personal, connections, exports, activities, interests, music, movies, tv, books, games, about, quotes, can_post, can_see_all_posts, can_see_audio, can_write_private_message, can_send_friend_request, is_favorite, is_hidden_from_feed, timezone, screen_name, maiden_name, crop_photo, is_friend, friend_status, career, military, blacklisted, blacklisted_by_me, can_be_invited_group");
            builder.userNameAttributeName("user_id");
            builder.clientName("vk");
            return builder;
        }
    },

    YANDEX {
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = this.getBuilder(registrationId, ClientAuthenticationMethod.POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
            builder.scope(new String[]{"login:birthday", "login:email", "login:info", "login:avatar"});
            builder.authorizationUri("https://oauth.yandex.ru/authorize");
            builder.tokenUri("https://oauth.yandex.ru/token");
            builder.userInfoUri("https://login.yandex.ru/info?format=json");
            builder.userNameAttributeName("id");
            builder.clientName("yandex");
            return builder;
        }
    };

    MyOAuth2Provider() {
    }

    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method, String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUriTemplate(redirectUri);
        return builder;
    }

    public abstract ClientRegistration.Builder getBuilder(String var1);

}
