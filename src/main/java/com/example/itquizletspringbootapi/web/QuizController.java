package com.example.itquizletspringbootapi.web;

import com.example.itquizletspringbootapi.dto.question.QuestionCreateDto;
import com.example.itquizletspringbootapi.dto.question.QuestionDto;
import com.example.itquizletspringbootapi.dto.question.QuestionUpdateDto;
import com.example.itquizletspringbootapi.dto.quiz.QuizCreateDto;
import com.example.itquizletspringbootapi.dto.quiz.QuizUpdateDto;
import com.example.itquizletspringbootapi.dto.userresponse.UserResponseDto;
import com.example.itquizletspringbootapi.repository.entity.Level;
import com.example.itquizletspringbootapi.repository.entity.QuestionEntity;
import com.example.itquizletspringbootapi.repository.entity.QuizEntity;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.service.QuestionService;
import com.example.itquizletspringbootapi.service.QuizService;
import com.example.itquizletspringbootapi.service.mapper.QuestionMapper;
import com.example.itquizletspringbootapi.service.mapper.QuizMapper;
import com.example.itquizletspringbootapi.web.decorators.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.itquizletspringbootapi.dto.quiz.QuizDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;


    @PostMapping
    @Operation(
            summary = "Create a new quiz",
            description = "Allows an authenticated user to create a new quiz",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz successfully created",
                    content = @Content(schema = @Schema(implementation = QuizDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or missing JWT token")
    })
    public ResponseEntity<QuizDto> createQuiz(
            @RequestBody QuizCreateDto quizCreateDTO,
            @CurrentUser UserEntity user
    ) {
        System.out.println(quizCreateDTO);
        QuizEntity createdQuiz = quizService.createQuiz(quizCreateDTO, user);
        return ResponseEntity.ok(quizMapper.toDTO(createdQuiz));
    }

    @Operation(
            summary = "Get quiz by ID",
            description = "Retrieve detailed information about a specific quiz by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuizDto.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable UUID id) throws BadRequestException {
        QuizEntity quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quizMapper.toDTO(quiz));
    }

    @Operation(
            summary = "Get all quizzes",
            description = "Retrieve a list of all quizzes with optional filtering by level and category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of quizzes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),
    })
    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes(
            @RequestParam(name = "level", required = false) Level level,
            @RequestParam(name = "category", required = false) Optional<String> category
    ) {
        List<QuizDto> allQuizzes = quizService.getAllQuizzes(level, category);
        return ResponseEntity.ok(allQuizzes);
    }

    @Operation(
            summary = "Update a quiz by ID",
            description = "Allows the owner of a quiz to update its details",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz updated successfully",
                    content = @Content(schema = @Schema(implementation = QuizDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not own the quiz"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<QuizDto> updateQuizById(@PathVariable UUID id, @RequestBody QuizUpdateDto quizUpdateDTO, @CurrentUser UserEntity user) throws BadRequestException {
        QuizEntity updatedQuiz = quizService.updateQuiz(id, quizUpdateDTO, user.getId());
        return ResponseEntity.ok(quizMapper.toDTO(updatedQuiz));
    }

    @Operation(
            summary = "Delete a quiz by ID",
            description = "Allows the owner of a quiz to delete it",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Quiz successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not own the quiz"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizById(@PathVariable UUID id, @CurrentUser UserEntity user) throws BadRequestException {
        quizService.deleteQuiz(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add a question to a quiz",
            description = "Allows the owner of a quiz to add a question to it",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question added successfully",
                    content = @Content(schema = @Schema(implementation = QuestionDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not own the quiz"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @PostMapping("/{id}/questions")
    public ResponseEntity<QuestionDto> addQuestionToQuiz(
            @PathVariable UUID id,
            @RequestBody @Valid QuestionCreateDto questionCreateDTO,
            @CurrentUser UserEntity user
    ) throws BadRequestException {
        quizService.checkOwner(id, user.getId());
        QuestionEntity addedQuestion = questionService.addQuestionToQuiz(id, questionCreateDTO);
        return ResponseEntity.ok(questionMapper.toDTO(addedQuestion));
    }

    @GetMapping("{id}/questions")
    @Operation(
            summary = "Get all questions by quiz ID",
            description = "Retrieve all questions associated with a specific quiz by its ID",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of questions retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestionDto.class)))),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<List<QuestionDto>> getQuestionsByQuiz(@PathVariable UUID id) {
        List<QuestionEntity> questions = questionService.getQuestionsByQuiz(id);
        return ResponseEntity.ok(
                questions.stream()
                        .map(questionMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}/questions/{questionId}")
    @Operation(
            summary = "Get question by ID",
            description = "Retrieve details of a specific question by its ID within a quiz",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuestionDto.class))),
            @ApiResponse(responseCode = "404", description = "Quiz or question not found")
    })
    public ResponseEntity<QuestionDto> getQuestionById(
            @PathVariable UUID id,
            @PathVariable UUID questionId
    ) throws BadRequestException {
        questionService.checkIfQuestionBelongsToQuiz(id, questionId);
        QuestionEntity question = questionService.getQuestionById(questionId);
        return ResponseEntity.ok(questionMapper.toDTO(question));
    }

    @PatchMapping("/{id}/questions/{questionId}")
    @Operation(
            summary = "Update question",
            description = "Update the details of a specific question within a quiz",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question updated successfully",
                    content = @Content(schema = @Schema(implementation = QuestionDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not own the quiz"),
            @ApiResponse(responseCode = "404", description = "Quiz or question not found")
    })
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable UUID id,
            @PathVariable UUID questionId,
            @RequestBody QuestionUpdateDto questionUpdateDto,
            @CurrentUser UserEntity user
    ) throws BadRequestException {
        quizService.checkOwner(id, user.getId());
        questionService.checkIfQuestionBelongsToQuiz(id, questionId);
        QuestionEntity updatedQuestion = questionService.updateQuestion(questionId, questionUpdateDto);
        return ResponseEntity.ok(questionMapper.toDTO(updatedQuestion));
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    @Operation(
            summary = "Delete question",
            description = "Delete a specific question within a quiz",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not own the quiz"),
            @ApiResponse(responseCode = "404", description = "Quiz or question not found")
    })
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable UUID id,
            @PathVariable UUID questionId,
            @CurrentUser UserEntity user
    ) throws BadRequestException {
        quizService.checkOwner(id, user.getId());
        questionService.checkIfQuestionBelongsToQuiz(id, questionId);
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
