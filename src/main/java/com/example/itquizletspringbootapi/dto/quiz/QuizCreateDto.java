package com.example.itquizletspringbootapi.dto.quiz;

import com.example.itquizletspringbootapi.repository.entity.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class QuizCreateDto {

    @NotBlank(message = "Title is mandatory.")
    String title;

    @NotBlank(message = "Description is mandatory.")
    String description;

    @NotNull(message = "Level is mandatory")
    Level level;

    @NotEmpty(message = "Answers list cannot be empty.")
    List<String> categories;

}
