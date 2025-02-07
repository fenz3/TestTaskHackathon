package com.example.itquizletspringbootapi.dto.user;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class UserDto {

    UUID id;
    String username;
    String avatarUrl;
    String email;

}
