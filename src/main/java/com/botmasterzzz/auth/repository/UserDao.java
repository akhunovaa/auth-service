package com.botmasterzzz.auth.repository;

import com.botmasterzzz.auth.model.Individual;
import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserAuthEntity;

import java.util.Optional;

public interface UserDao {

    Long userAdd(User user);

    Long userAuthEntityAdd(UserAuthEntity userAuthEntity);

    void userUpdate(User user);

    Optional<User> findByLogin(String login);

    Optional<User> findByProviderLogin(String login, Enum provider);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    Optional<Individual> findByIndividualId(Long id);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

    void individualUpdate(Individual individual);

}
