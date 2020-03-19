package com.botmasterzzz.auth.security.oauth2.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class VkOAuth2UserInfo extends OAuth2UserInfo {
    public VkOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("user_id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("first_name");
    }

    public String getLastName() {
        return (String) attributes.get("last_name");
    }

    public Integer getSex() {
        return (Integer) attributes.get("sex");
    }

    public String getNickName() {
        return (String) attributes.get("nickname");
    }

    public Date getBirthDate() throws ParseException {
        String stringDate = (String) attributes.get("bdate");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.d.YYYY");
        return dateFormat.parse(stringDate);
    }

    @SuppressWarnings("unchecked")
    public String getCity() {
        if (attributes.containsKey("city")){
            return (String) ((LinkedHashMap)attributes.get("city")).getOrDefault("title", "Москва");
        }else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String getCountry() {
        if (attributes.containsKey("country")){
            return (String) ((LinkedHashMap)attributes.get("country")).getOrDefault("title", "Россия");
        }else {
            return null;
        }
    }

    public String getOriginalPhotoUrl() {
        return (String) attributes.get("photo_max");
    }

    public String getHomePhone() {
        return (String) attributes.get("home_phone");
    }

    public String getSite() {
        return (String) attributes.get("site");
    }

    public String getAbout() {
        return (String) attributes.get("about");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        if (attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if (pictureObj.containsKey("data")) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }
}