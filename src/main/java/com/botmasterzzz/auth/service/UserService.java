package com.botmasterzzz.auth.service;

import com.botmasterzzz.auth.dto.UserDTO;

public interface UserService {

    UserDTO save(UserDTO userDTO);

    void send(UserDTO userDTO);

    void consume(UserDTO userDTO);

    void imageUrlUpdate(UserDTO userDTO);

    void passwordUpdate(UserDTO userDTO);
}
