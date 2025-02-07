package com.example.itquizletspringbootapi.dto.userresponse;

import com.example.itquizletspringbootapi.dto.quiz.QuizDto;
import com.example.itquizletspringbootapi.dto.user.UserDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class UserResponseDto {
    UUID id;
    UserDto user;
    QuizDto quiz;
}
