package com.botmasterzzz.auth.payload;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginRequest {

    @NotNull(message = "Логин не долджен быть пустым")
    @Size(min = 3, max = 50, message = "Логин должен состоять как минимум из 3 символов")
    private String login;

    @NotNull(message = "Пароль не долджен быть пустым")
    @Size(min = 6, max = 255, message = "Пароль должен состоять как минимум из 3 символов")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}