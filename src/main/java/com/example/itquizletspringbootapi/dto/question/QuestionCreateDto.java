package com.example.itquizletspringbootapi.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Value
@Builder
@Jacksonized
public class QuestionCreateDto implements QuestionData {
    @NotBlank(message = "Question text is mandatory.")
    String text;

    @NotBlank(message = "CorrectAnswer text is mandatory.")
    String correctAnswer;

    @NotEmpty(message = "Variants list cannot be empty.")
    List<String> variants;
}
