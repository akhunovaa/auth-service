package com.botmasterzzz.auth.payload;

import javax.validation.constraints.Size;

public class SignUpRequest {

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String surname;

    @Size(max = 50)
    private String patrName;

    @Size(min = 3, max = 50)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(min = 3, max = 50)
    private String login;

    @Size(min = 3, max = 255)
    private String password;

    @Size(min = 3, max = 300)
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