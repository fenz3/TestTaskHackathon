package com.example.itquizletspringbootapi.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserLoginDto {

    @NotBlank(message = "Username is mandatory.")
    String username;

    @NotBlank(message = "Password is mandatory.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "The password must contain at least 8 characters, one capital letter and one digit.")
    String password;
}
