package com.botmasterzzz.auth.service.impl;

import com.botmasterzzz.auth.dto.UserDTO;
import com.botmasterzzz.auth.entity.User;
import com.botmasterzzz.auth.repository.UserDao;
import com.botmasterzzz.auth.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Value(value = "${user.topic.name}")
    private String userTopicName;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO save(UserDTO userDTO) {
        return null;
    }

    @Override
    public void send(UserDTO userDTO) {
        LOGGER.info("<= sending {}", writeValueAsString(userDTO));
    }

    @Override
    public void consume(UserDTO userDTO) {
        LOGGER.info("=> consumed {}", writeValueAsString(userDTO));
    }

    @Override
    @KafkaListener(topics = "${user.topic.name}", containerFactory = "singleFactory")
    public void imageUrlUpdate(UserDTO userDTO) {
        String imageUrl = userDTO.getImageUrl();
        User user = userDao.findByLogin(userDTO.getLogin())
                .orElseGet(() -> userDao.findById(userDTO.getId())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with login : " + userDTO.getLogin() + " or email: " + userDTO.getId())));
        user.setImageUrl(imageUrl);
        LOGGER.info("=> consumed {}", writeValueAsString(userDTO));
        userDao.userUpdate(user);
        LOGGER.info("User image was updated {}", imageUrl);
    }

    @Override
    @KafkaListener(topics = "${user.password.topic.name}", containerFactory = "passwordFactory")
    public void passwordUpdate(UserDTO userDTO) {
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = userDao.findByLogin(userDTO.getLogin())
                .orElseGet(() -> userDao.findById(userDTO.getId())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with login : " + userDTO.getLogin() + " or email: " + userDTO.getId())));
        user.setPassword(encodedPassword);
        LOGGER.info("=> consumed {}", writeValueAsString(userDTO));
        userDao.userUpdate(user);
        LOGGER.info("User password was updated to login: {}", userDTO.getLogin());
    }


    private String writeValueAsString(UserDTO userDTO) {
        try {
            return objectMapper.writeValueAsString(userDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Writing value to JSON failed: " + userDTO.toString());
        }
    }
}
