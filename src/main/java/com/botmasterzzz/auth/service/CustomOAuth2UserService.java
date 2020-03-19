package com.botmasterzzz.auth.service;

import com.botmasterzzz.auth.exception.OAuth2AuthenticationProcessingException;
import com.botmasterzzz.auth.model.AuthProvider;
import com.botmasterzzz.auth.model.Individual;
import com.botmasterzzz.auth.model.UserRole;
import com.botmasterzzz.auth.repository.UserDao;
import com.botmasterzzz.auth.security.UserPrincipal;
import com.botmasterzzz.auth.security.oauth2.user.OAuth2UserInfo;
import com.botmasterzzz.auth.security.oauth2.user.OAuth2UserInfoFactory;
import com.botmasterzzz.auth.security.oauth2.user.VkOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import com.botmasterzzz.auth.model.User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.text.ParseException;
import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User;
        if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals("vk")) {
            oAuth2User = loadVkUser(oAuth2UserRequest);
        } else {
            oAuth2User = super.loadUser(oAuth2UserRequest);
        }
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException("Request error occurs", ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        String email = null != oAuth2UserInfo.getEmail() ? oAuth2UserInfo.getEmail() : (String) oAuth2UserRequest.getAdditionalParameters().get("email");
        if(StringUtils.isEmpty(email)) {
            email = oAuth2UserRequest.getAdditionalParameters().get("user_id") + "@" + oAuth2UserRequest.getClientRegistration().getRegistrationId() + ".com";
//            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        String registrationProvider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Optional<User> userOptional = userDao.findByProviderLogin(email, AuthProvider.valueOf(registrationProvider));
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals("vk")) {
                user = registerNewUserFromVk(oAuth2UserRequest, oAuth2UserInfo);
            } else {
                user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
            }
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        String fulledName = oAuth2UserInfo.getName();
        String name = oAuth2UserInfo.getName().contains(" ") ? oAuth2UserInfo.getName().split(" ")[0] : fulledName;
        String surname = oAuth2UserInfo.getName().contains(" ") ? oAuth2UserInfo.getName().split(" ")[1] : fulledName;
        UserRole userRole = new UserRole();
        userRole.setId(4L);
        userRole.setRoleName("USER");
        User user = new User();
        Individual individual = new Individual();
        user.setUserRole(userRole);
        user.setLogin(oAuth2UserInfo.getEmail());
        user.setName(name);
        user.setSurname(surname);
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setPassword(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        user.setNote(oAuth2UserInfo.getId());
        Long id = userDao.userAdd(user);
        user.setId(id);
        individual.setId(id);
        individual.setName(name);
        individual.setSurname(surname);
        individual.setNickname(fulledName);
        individual.setDeleted(false);
        individual.setImageUrl(oAuth2UserInfo.getImageUrl());
        userDao.individualUpdate(individual);
        return user;
    }

    private User registerNewUserFromVk(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        VkOAuth2UserInfo vkOAuth2UserInfo = (VkOAuth2UserInfo) oAuth2UserInfo;
        String name = vkOAuth2UserInfo.getName();
        String surname = vkOAuth2UserInfo.getLastName();
        String sex = vkOAuth2UserInfo.getSex() == 2 ? "Мужской" : "Женский";
        String nickname = vkOAuth2UserInfo.getNickName();
        Date birthDate = null;
        try {
            birthDate = new Date(vkOAuth2UserInfo.getBirthDate().getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String city = vkOAuth2UserInfo.getCity();
        String country = vkOAuth2UserInfo.getCountry();
        String pictureUrl = vkOAuth2UserInfo.getOriginalPhotoUrl();
        String phone = vkOAuth2UserInfo.getHomePhone();
        String about = vkOAuth2UserInfo.getAbout();
        String email = null != oAuth2UserInfo.getEmail() ? oAuth2UserInfo.getEmail() : (String) oAuth2UserRequest.getAdditionalParameters().get("email");
        if(StringUtils.isEmpty(email)) {
            email = oAuth2UserRequest.getAdditionalParameters().get("user_id") + "@" + oAuth2UserRequest.getClientRegistration().getRegistrationId() + ".com";
        }
        UserRole userRole = new UserRole();
        userRole.setId(4L);
        userRole.setRoleName("USER");
        User user = new User();
        Individual individual = new Individual();
        user.setUserRole(userRole);
        user.setLogin(email);
        user.setName(name);
        user.setSurname(surname);
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setEmail(email);
        user.setImageUrl(pictureUrl);
        user.setPassword(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        user.setNote(oAuth2UserInfo.getId());
        Long id = userDao.userAdd(user);
        user.setId(id);
        individual.setId(id);
        individual.setName(name);
        individual.setSurname(surname);
        individual.setNickname(nickname);
        individual.setDeleted(false);
        individual.setImageUrl(pictureUrl);
        individual.setBirthDate(birthDate);
        individual.setPhone(phone);
        individual.setCity(city + ", " + country);
        individual.setInfo(about);
        individual.setGender(sex);
        userDao.individualUpdate(individual);
        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        String fulledName = oAuth2UserInfo.getName();
        String name = oAuth2UserInfo.getName().contains(" ") ? oAuth2UserInfo.getName().split(" ")[0] : fulledName;
        String surname = oAuth2UserInfo.getName().contains(" ") ? oAuth2UserInfo.getName().split(" ")[1] : fulledName;
        existingUser.setName(name);
        existingUser.setSurname(surname);
        userDao.userUpdate(existingUser);
        Optional<Individual> optionalIndividual = userDao.findByIndividualId(existingUser.getId());
        if (optionalIndividual.isPresent()){
            Individual individual = optionalIndividual.get();
            individual.setName(name);
            individual.setSurname(surname);
            userDao.individualUpdate(individual);
        }
        return existingUser;
    }

    @SuppressWarnings("unchecked")
    private OAuth2User loadVkUser(OAuth2UserRequest oAuth2UserRequest)  {
        RestTemplate template = new RestTemplate();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("Content-Type", "application/json");
        HttpEntity<?> httpRequest = new HttpEntity(headers);
        String uri = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        uri = uri.replace("{user_id}", userNameAttributeName + "=" + oAuth2UserRequest.getAdditionalParameters().get(userNameAttributeName));
        uri = uri.replace("{access_token}", "access_token" + "=" + oAuth2UserRequest.getAccessToken().getTokenValue());

        try {
            ResponseEntity<Object> entity = template.exchange(uri, HttpMethod.GET, httpRequest, Object.class);
            Map<String, Object> response = (Map) entity.getBody();
            ArrayList valueList = (ArrayList) response.get("response");
            Map<String, Object> userAttributes = (Map<String, Object>) valueList.get(0);
            userAttributes.put(userNameAttributeName, oAuth2UserRequest.getAdditionalParameters().get(userNameAttributeName));

            Set<GrantedAuthority> authorities = Collections.singleton(new OAuth2UserAuthority(userAttributes));
            return new DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
        } catch (HttpClientErrorException ex) {
            throw new InternalAuthenticationServiceException("Request error occurs", ex.getCause());
        }
    }
}
