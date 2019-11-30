package com.botmasterzzz.auth.service.impl;

import com.botmasterzzz.auth.dto.UserDTO;
import com.botmasterzzz.auth.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Value(value = "${user.topic.name}")
    private String userTopicName;

    @Override
    public UserDTO save(UserDTO userDTO) {
        return null;
    }

    @Override
    public void send(UserDTO userDTO) {
        LOGGER.info("<= sending {}", writeValueAsString(userDTO));
    }

    @Override
    @KafkaListener(topics = "${user.topic.name}", groupId = "${user.group.id}", containerFactory = "singleFactory")
    public void consume(UserDTO userDTO) {
        LOGGER.info("=> consumed {}", writeValueAsString(userDTO));
    }

    private String writeValueAsString(UserDTO userDTO) {
        try {
            return objectMapper.writeValueAsString(userDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Writing value to JSON failed: " + userDTO.toString());
        }
    }
}
