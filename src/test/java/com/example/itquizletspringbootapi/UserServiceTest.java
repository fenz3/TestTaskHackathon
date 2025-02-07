package com.example.itquizletspringbootapi;

import com.example.itquizletspringbootapi.dto.user.UserUpdateDto;
import com.example.itquizletspringbootapi.repository.UserRepository;
import com.example.itquizletspringbootapi.repository.SavedQuizzes;
import com.example.itquizletspringbootapi.repository.QuizRepository;
import com.example.itquizletspringbootapi.repository.entity.*;
import com.example.itquizletspringbootapi.service.UserService;
import com.example.itquizletspringbootapi.service.mapper.QuizMapper;
import com.example.itquizletspringbootapi.service.mapper.UserMapper;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SavedQuizzes savedQuizzesRepository;
    @MockBean
    private QuizRepository quizRepository;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private QuizMapper quizMapper;
    @Autowired
    private UserService userService;

    private UUID userId;
    private UUID quizId;
    private UserEntity user;
    private QuizEntity quiz;
    private SavedQuizId savedQuizId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        quizId = UUID.randomUUID();
        user = new UserEntity();
        user.setId(userId);
        quiz = new QuizEntity();
        quiz.setId(quizId);
        savedQuizId = new SavedQuizId(userId, quizId);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() throws BadRequestException {
        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        UserEntity result = userService.getUserById(userId);

        assertEquals(mockUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.getUserById(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_shouldUpdateUser_whenUserExists() throws BadRequestException {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .username("UpdatedUsername")
                .avatarUrl("https://example.com/avatar2.png")
                .build();
        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setUsername("OldName");
        existingUser.setAvatarUrl("https://example.com/avatar.png");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setUsername("UpdatedUsername");
        updatedUser.setAvatarUrl("https://example.com/avatar2.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.updateUserFromDto(userUpdateDto, existingUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserEntity result = userService.updateUser(userId, userUpdateDto);

        assertNotNull(result);
        assertEquals("UpdatedUsername", result.getUsername());
        assertEquals("https://example.com/avatar2.png", result.getAvatarUrl());
        verify(userRepository).findById(userId);
        verify(userMapper).updateUserFromDto(userUpdateDto, existingUser);
        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .username("UpdatedUsername")
                .avatarUrl("https://example.com/avatar2.png")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.updateUser(userId, userUpdateDto));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() throws BadRequestException {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.existsById(userId)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found with Id: " + userId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void toggleSavedQuiz_shouldRemoveQuizFromSaved_whenQuizAlreadySaved() throws BadRequestException {
        when(savedQuizzesRepository.existsById(savedQuizId)).thenReturn(true);

        userService.toggleSavedQuiz(quizId, user);

        verify(savedQuizzesRepository).existsById(savedQuizId);
        verify(savedQuizzesRepository).deleteById(savedQuizId);
    }

    @Test
    void toggleSavedQuiz_shouldAddQuizToSaved_whenQuizNotAlreadySaved() throws BadRequestException {
        SavedQuizEntity savedQuiz = new SavedQuizEntity(savedQuizId, user, quiz);

        when(savedQuizzesRepository.existsById(savedQuizId)).thenReturn(false);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        userService.toggleSavedQuiz(quizId, user);

        verify(savedQuizzesRepository).existsById(savedQuizId);
        verify(quizRepository).findById(quizId);
        verify(savedQuizzesRepository).save(savedQuiz);
    }

    @Test
    void toggleSavedQuiz_shouldThrowException_whenQuizNotFound() {
        when(savedQuizzesRepository.existsById(savedQuizId)).thenReturn(false);
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.toggleSavedQuiz(quizId, user));

        assertEquals("Quiz not found", exception.getMessage());
        verify(savedQuizzesRepository).existsById(savedQuizId);
        verify(quizRepository).findById(quizId);
    }
}
