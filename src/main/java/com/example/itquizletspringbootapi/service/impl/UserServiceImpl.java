package com.example.itquizletspringbootapi.service.impl;

import com.example.itquizletspringbootapi.dto.quiz.QuizDto;
import com.example.itquizletspringbootapi.dto.user.UserUpdateDto;
import com.example.itquizletspringbootapi.repository.QuizRepository;
import com.example.itquizletspringbootapi.repository.SavedQuizzes;
import com.example.itquizletspringbootapi.repository.UserRepository;
import com.example.itquizletspringbootapi.repository.entity.QuizEntity;
import com.example.itquizletspringbootapi.repository.entity.SavedQuizEntity;
import com.example.itquizletspringbootapi.repository.entity.SavedQuizId;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.service.UserService;
import com.example.itquizletspringbootapi.service.mapper.QuizMapper;
import com.example.itquizletspringbootapi.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SavedQuizzes savedQuizzesRepository;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    public UserEntity getUserById(UUID id) throws BadRequestException {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + id));
    }

    @Override
    public UserEntity updateUser(UUID id, UserUpdateDto updatedUserDto) throws BadRequestException {
        UserEntity userEntity = this.getUserById(id);

        UserEntity user = userMapper.updateUserFromDto(updatedUserDto, userEntity);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) throws BadRequestException {

        if (!userRepository.existsById(id)) {
            throw new BadRequestException("User not found with Id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public void toggleSavedQuiz(UUID quizId, UserEntity user) throws BadRequestException {
        SavedQuizId savedQuizId = new SavedQuizId(user.getId(), quizId);

        if (savedQuizzesRepository.existsById(savedQuizId)) {
            savedQuizzesRepository.deleteById(savedQuizId);
            return;
        }
        QuizEntity quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BadRequestException("Quiz not found"));

        SavedQuizEntity savedQuiz = SavedQuizEntity.builder()
                .id(savedQuizId)
                .user(user)
                .quiz(quiz)
                .build();
        savedQuizzesRepository.save(savedQuiz);
    }

    public List<QuizDto> getSavedQuizzesList(UserEntity user) {
        List<SavedQuizEntity> savedQuizzes = savedQuizzesRepository.findAllByIdUserId(user.getId());
        return savedQuizzes.stream()
                .map(savedQuiz -> quizMapper.toDTO(savedQuiz.getQuiz()))
                .collect(Collectors.toList());
    }

}
