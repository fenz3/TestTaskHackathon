package com.example.itquizletspringbootapi.web;

import com.example.itquizletspringbootapi.dto.quiz.QuizDto;
import com.example.itquizletspringbootapi.dto.user.UserDto;
import com.example.itquizletspringbootapi.dto.user.UserUpdateDto;
import com.example.itquizletspringbootapi.dto.userresponse.UserResponseDto;
import com.example.itquizletspringbootapi.repository.entity.QuizEntity;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.repository.entity.UserResponseEntity;
import com.example.itquizletspringbootapi.service.QuizService;
import com.example.itquizletspringbootapi.service.UserResponseService;
import com.example.itquizletspringbootapi.service.UserService;
import com.example.itquizletspringbootapi.service.mapper.QuizMapper;
import com.example.itquizletspringbootapi.service.mapper.UserMapper;
import com.example.itquizletspringbootapi.service.mapper.UserResponseMapper;
import com.example.itquizletspringbootapi.web.decorators.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final QuizService quizService;
    private final UserMapper userMapper;
    private final QuizMapper quizMapper;
    private final UserResponseService userResponseService;
    private final UserResponseMapper userResponseMapper;

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve detailed information about a user by their ID",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) throws BadRequestException {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @Operation(
            summary = "Update current user",
            description = "Update the details of the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UserUpdateDto userUpdateDTO,
            @CurrentUser UserEntity user
    ) throws BadRequestException {
        UserEntity updatedUser = userService.updateUser(user.getId(), userUpdateDTO);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @Operation(
            summary = "Delete current user",
            description = "Delete the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User not authorized")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@CurrentUser UserEntity user) throws BadRequestException {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get quizzes by owner",
            description = "Retrieve all quizzes created by the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuizDto.class)))
    })
    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizDto>> getQuizzesByOwner(@CurrentUser UserEntity user) {
        List<QuizEntity> quizzes = quizService.getQuizzesByOwner(user.getId());
        List<QuizDto> mappedQuizzes = quizzes.stream()
                .map(quizMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mappedQuizzes);
    }

    @Operation(
            summary = "Toggle saved quiz",
            description = "Save or unsave a quiz for the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz saved or unsaved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    @PostMapping("/savedQuizzes/toggle/{quizId}")
    public ResponseEntity<Void> toggleSavedQuiz(
            @PathVariable UUID quizId,
            @CurrentUser UserEntity user
    ) throws BadRequestException {
        userService.toggleSavedQuiz(quizId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get saved quizzes",
            description = "Retrieve all quizzes saved by the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saved quizzes retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuizDto.class)))
    })
    @GetMapping("/savedQuizzes")
    public ResponseEntity<List<QuizDto>> getSavedQuizzes(@CurrentUser UserEntity user) {
        List<QuizDto> mappedQuizzes = userService.getSavedQuizzesList(user);
        return ResponseEntity.ok(mappedQuizzes);
    }

    @Operation(
            summary = "Get user responses",
            description = "Retrieve all responses submitted by the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User responses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    })
    @GetMapping("/responses")
    public ResponseEntity<List<UserResponseDto>> getUserResponses(@CurrentUser UserEntity user) {
        List<UserResponseEntity> responses = userResponseService.getResponsesByUser(user);
        return ResponseEntity.ok(
                responses.stream()
                        .map(userResponseMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
