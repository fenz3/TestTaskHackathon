package com.example.itquizletspringbootapi.dto.question;

import java.util.List;

public interface QuestionData {
    List<String> getVariants();
    String getCorrectAnswer();
}
