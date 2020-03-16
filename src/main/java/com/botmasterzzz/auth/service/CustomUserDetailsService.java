package com.botmasterzzz.auth.service;

import com.botmasterzzz.auth.exception.OAuth2AuthenticationProcessingException;
import com.botmasterzzz.auth.model.AuthProvider;
import com.botmasterzzz.auth.model.Individual;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserRole;
import com.botmasterzzz.auth.repository.UserDao;
import com.botmasterzzz.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userDao.findByLogin(login)
                .orElseGet(() -> userDao.findByEmail(login)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with login : " + login)));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));

        return UserPrincipal.create(user);
    }

    public UserDetails processUser(Authentication authentication) {
        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
        Map attributes = oidcUser.getAttributes();
        String email = (String) attributes.get("email");
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        Optional<User> userOptional = userDao.findByProviderLogin(email, AuthProvider.valueOf(provider));
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(provider))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(oidcUser, user);
        } else {
            user = registerNewUser(oidcUser, provider);
        }
        return UserPrincipal.create(user);
    }

    private User registerNewUser(DefaultOidcUser oidcUser, String provider) {
        Map attributes = oidcUser.getAttributes();
        String email = (String) attributes.get("email");
        String givenName = (String) attributes.get("given_name");
        String surname = (String) attributes.get("family_name");
        String picture = (String) attributes.get("picture");
        String locale = (String) attributes.get("locale");
        String fulledName = (String) attributes.get("name");
        UserRole userRole = new UserRole();
        userRole.setId(4L);
        User user = new User();
        Individual individual = new Individual();
        user.setUserRole(userRole);
        user.setLogin(email);
        user.setPassword(provider);
        user.setProvider(AuthProvider.valueOf(provider));
        user.setName(givenName);
        user.setEmail(email);
        user.setSurname(surname);
        user.setImageUrl(picture);
        Long id = userDao.userAdd(user);
        user.setId(id);
        individual.setId(id);
        individual.setLanguage(locale);
        individual.setName(givenName);
        individual.setSurname(surname);
        individual.setNickname(fulledName);
        individual.setDeleted(false);
        individual.setImageUrl(picture);
        userDao.individualUpdate(individual);
        return user;
    }

    private User updateExistingUser(DefaultOidcUser oidcUser, User existingUser) {
        Map attributes = oidcUser.getAttributes();
        String givenName = (String) attributes.get("given_name");
        String picture = (String) attributes.get("picture");
        existingUser.setName(givenName);
        existingUser.setImageUrl(picture);
        userDao.userUpdate(existingUser);
        Optional<Individual> optionalIndividual = userDao.findByIndividualId(existingUser.getId());
        if (optionalIndividual.isPresent()){
            Individual individual = optionalIndividual.get();
            individual.setImageUrl(existingUser.getImageUrl());
            userDao.individualUpdate(individual);
        }
        return existingUser;
    }


}