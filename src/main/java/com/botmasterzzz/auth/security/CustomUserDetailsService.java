package com.botmasterzzz.auth.security;

import com.botmasterzzz.auth.exception.ResourceNotFoundException;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        User user;
        if (userRepository.findByEmail(login).isPresent()){
            user = userRepository.findByEmail(login).get();
        }else {
           user  = userRepository.findByLogin(login).orElseThrow(() ->
                    new UsernameNotFoundException("User not found with login : " + login));
        }

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}