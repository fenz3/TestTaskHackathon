package com.example.itquizletspringbootapi.dto.user;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.URL;

@Value
@Builder
@Jacksonized
public class UserUpdateDto {
    String username;

    @URL(message = "Invalid format of the avatar URL.")
    String avatarUrl;
}
