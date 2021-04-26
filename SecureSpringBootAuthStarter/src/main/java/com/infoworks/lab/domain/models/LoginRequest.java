package com.infoworks.lab.domain.models;

import com.infoworks.lab.rest.models.Message;

import javax.validation.constraints.NotEmpty;

public class LoginRequest extends Message {

    @NotEmpty(message = "Username must not null or empty!")
    private String username;

    @NotEmpty(message = "Password must not null or empty!")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
