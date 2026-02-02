package com.infoworks.lab.domain.models;

import com.infoworks.lab.domain.validations.Password.PasswordRule;
import com.infoworks.objects.Message;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class NewAccountRequest extends Message {

    @NotEmpty(message = "Username must not null or empty!")
    private String username;

    @PasswordRule(minLengthRule = 3, maxLengthRule = 8)
    @NotEmpty(message = "Password must not null or empty!")
    private String password;

    @Email(message = "Invalid email address")
    private String email;

    private String mobile;

    public NewAccountRequest(String username, String password, String email, String mobile) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
    }

    public NewAccountRequest() {}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
