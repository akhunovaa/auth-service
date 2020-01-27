package com.botmasterzzz.auth.repository;

import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserAuthEntity;

import java.util.Optional;

public interface UserDao {

    Long userAdd(User user);

    Long userAuthEntityAdd(UserAuthEntity userAuthEntity);

    void userUpdate(User user);

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

}
