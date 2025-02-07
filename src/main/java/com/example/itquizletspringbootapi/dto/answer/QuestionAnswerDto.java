package com.example.itquizletspringbootapi.dto.answer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class QuestionAnswerDto {
    UUID id;
    String answer;
    Boolean isCorrect;
}
