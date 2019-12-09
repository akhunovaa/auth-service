package com.botmasterzzz.auth.payload;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SignUpRequest {

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String surname;

    @Size(max = 50)
    private String patrName;

    @Size(min = 6, max = 50, message = "Email должен состоять как минимум из 6 символов")
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(min = 3, max = 50, message = "Логин должен состоять как минимум из 3 символов")
    @NotNull(message = "Логин не долджен быть пустым")
    private String login;

    @Size(min = 6, max = 255, message = "Пароль должен состоять как минимум из 6 символов")
    @NotNull(message = "Пароль не долджен быть пустым")
    private String password;

    @Size(min = 3, max = 300, message = "Капча должна состоять как минимум из 3 символов")
    private String captchaToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatrName() {
        return patrName;
    }

    public void setPatrName(String patrName) {
        this.patrName = patrName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
}