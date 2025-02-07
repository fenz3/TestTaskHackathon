package com.example.itquizletspringbootapi.service;

import com.example.itquizletspringbootapi.repository.entity.QuestionAnswerEntity;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.repository.entity.UserResponseEntity;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;

public interface UserResponseService {

    UserResponseEntity createUserResponse(UserEntity user, UUID quizId) throws BadRequestException;
    List<UserResponseEntity> getResponsesByUser(UserEntity user);
    List<UserResponseEntity> getResponsesByQuiz(UUID quizId);
    QuestionAnswerEntity addAnswer(UUID responseId, UUID questionId, String answer) throws BadRequestException;

}
