package com.example.itquizletspringbootapi.dto.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class QuestionAnswerCreateDto {
    @NotBlank
    String answer;
}
