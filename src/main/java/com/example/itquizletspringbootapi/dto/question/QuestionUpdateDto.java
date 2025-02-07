package com.example.itquizletspringbootapi.dto.question;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@Value
@Builder
@Jacksonized
public class QuestionUpdateDto implements QuestionData {
    String text;
    String correctAnswer;
    List<String> variants;
}
