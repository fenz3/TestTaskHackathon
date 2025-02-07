package com.example.itquizletspringbootapi.web;

import com.example.itquizletspringbootapi.dto.answer.QuestionAnswerCreateDto;
import com.example.itquizletspringbootapi.dto.answer.QuestionAnswerDto;
import com.example.itquizletspringbootapi.dto.userresponse.UserResponseDto;
import com.example.itquizletspringbootapi.repository.entity.QuestionAnswerEntity;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.repository.entity.UserResponseEntity;
import com.example.itquizletspringbootapi.service.UserResponseService;
import com.example.itquizletspringbootapi.service.mapper.QuestionAnswerMapper;
import com.example.itquizletspringbootapi.service.mapper.UserResponseMapper;
import com.example.itquizletspringbootapi.web.decorators.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/responses")
@RequiredArgsConstructor
public class UserResponseController {

    private final UserResponseService userResponseService;
    private final UserResponseMapper userResponseMapper;
    private final QuestionAnswerMapper questionAnswerMapper;

    @Operation(
            summary = "Create user response",
            description = "Create a new response for a quiz by the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Response created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUserResponse(
            @RequestParam UUID quizId,
            @CurrentUser UserEntity user
    ) throws BadRequestException {
        UserResponseEntity userResponse = userResponseService.createUserResponse(user, quizId);
        return ResponseEntity.ok(userResponseMapper.toDto(userResponse));
    }

    @Operation(
            summary = "Add answer to response",
            description = "Add an answer to an existing response by the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer added successfully",
                    content = @Content(schema = @Schema(implementation = QuestionAnswerDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Response or question not found")
    })
    @PostMapping("/{responseId}/answers")
    public ResponseEntity<QuestionAnswerDto> addAnswer(
            @PathVariable UUID responseId,
            @RequestParam UUID questionId,
            @RequestBody @Valid QuestionAnswerCreateDto answerCreateDto
    ) throws BadRequestException {
        QuestionAnswerEntity answer = userResponseService.addAnswer(responseId, questionId, answerCreateDto.getAnswer());
        return ResponseEntity.ok(questionAnswerMapper.toDto(answer));
    }

    @Operation(
            summary = "Get responses by quiz",
            description = "Retrieve all responses for a specific quiz",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Responses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @GetMapping("/by-quiz/{quizId}")
    public ResponseEntity<List<UserResponseDto>> getResponsesByQuiz(@PathVariable UUID quizId) {
        List<UserResponseEntity> responses = userResponseService.getResponsesByQuiz(quizId);
        return ResponseEntity.ok(
                responses.stream()
                        .map(userResponseMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
