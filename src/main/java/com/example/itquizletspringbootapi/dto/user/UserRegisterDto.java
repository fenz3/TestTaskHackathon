package com.example.itquizletspringbootapi.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.URL;

@Value
@Builder
@Jacksonized
public class UserRegisterDto {

    @NotBlank(message = "Username is mandatory.")
    String username;

    @NotBlank(message = "Email is mandatory.")
    @Email(message = "Invalid format of the email.")
    String email;

    @NotBlank(message = "Password is mandatory.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
             message = "The password must contain at least 8 characters, one capital letter and one digit.")
    String password;

    @URL(message = "Invalid format of the avatar URL.")
    String avatarUrl;
}
