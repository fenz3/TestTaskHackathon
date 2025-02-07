package com.example.itquizletspringbootapi.service;

import com.example.itquizletspringbootapi.dto.user.*;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;

public interface AuthService {

    AuthenticationDto register(UserRegisterDto request);
    AuthenticationDto login(UserLoginDto request);
    UserEntity getUser(String username);
}
