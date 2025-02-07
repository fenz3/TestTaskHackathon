package com.example.itquizletspringbootapi.service;

import com.example.itquizletspringbootapi.dto.question.QuestionCreateDto;
import com.example.itquizletspringbootapi.dto.question.QuestionData;
import com.example.itquizletspringbootapi.dto.question.QuestionUpdateDto;
import com.example.itquizletspringbootapi.repository.entity.QuestionEntity;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    void checkIfQuestionBelongsToQuiz(UUID quizId, UUID questionId) throws BadRequestException;
    void checkAnswer(QuestionData question) throws BadRequestException;
    QuestionEntity addQuestionToQuiz(UUID quizId, QuestionCreateDto question) throws BadRequestException;
    QuestionEntity getQuestionById(UUID questionId) throws BadRequestException;
    List<QuestionEntity> getQuestionsByQuiz(UUID quizId);
    QuestionEntity updateQuestion(UUID questionId, QuestionUpdateDto updatedQuestion) throws BadRequestException;
    void deleteQuestion(UUID questionId) throws BadRequestException;

}
