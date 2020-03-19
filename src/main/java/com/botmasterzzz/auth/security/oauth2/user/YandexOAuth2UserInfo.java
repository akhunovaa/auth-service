package com.botmasterzzz.auth.security.oauth2.user;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class YandexOAuth2UserInfo extends OAuth2UserInfo {

    public YandexOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("first_name");
    }

    public String getLastName() {
        return (String) attributes.get("last_name");
    }

    public String getDisplayName() {
        return (String) attributes.get("display_name");
    }

    public String getRealName() {
        return (String) attributes.get("real_name");
    }

    public Boolean isAvatarEmpty() {
        return (Boolean) attributes.get("is_avatar_empty");
    }

    public String getSex() {
        return (String) attributes.get("sex");
    }

    public String getLogin() {
        return (String) attributes.get("login");
    }

    public Date getBirthDate() throws ParseException {
        String stringDate = (String) attributes.get("birthday");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = dateFormat.parse(stringDate);
        return new Date(date.getTime());
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("default_email");
    }

    @Override
    public String getImageUrl() {
        if (attributes.containsKey("default_avatar_id")) {
            String pictureId = (String) attributes.get("default_avatar_id");
            return "https://avatars.yandex.net/get-yapic/" + pictureId + "/islands-200";
        }
        return null;
    }
}