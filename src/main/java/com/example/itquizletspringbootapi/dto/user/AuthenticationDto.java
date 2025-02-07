package com.example.itquizletspringbootapi.dto.user;

public class AuthenticationDto {
    private String token;

    public AuthenticationDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
