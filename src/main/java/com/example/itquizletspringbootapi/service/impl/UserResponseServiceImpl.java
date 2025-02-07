package com.example.itquizletspringbootapi.service.impl;

import com.example.itquizletspringbootapi.repository.AnswerRepository;
import com.example.itquizletspringbootapi.repository.UserResponseRepository;
import com.example.itquizletspringbootapi.repository.entity.*;
import com.example.itquizletspringbootapi.service.QuestionService;
import com.example.itquizletspringbootapi.service.QuizService;
import com.example.itquizletspringbootapi.service.UserResponseService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserResponseServiceImpl implements UserResponseService {

    private final UserResponseRepository userResponseRepository;
    private final QuizService quizService;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;

    @Override
    public UserResponseEntity createUserResponse(UserEntity user, UUID quizId) throws BadRequestException {
        QuizEntity quiz = quizService.getQuizById(quizId);

        UserResponseEntity response = new UserResponseEntity();
        response.setUser(user);
        response.setQuiz(quiz);

        return userResponseRepository.save(response);
    }

    @Override
    public List<UserResponseEntity> getResponsesByUser(UserEntity user) {
        return userResponseRepository.findByUser(user);
    }

    @Override
    public List<UserResponseEntity> getResponsesByQuiz(UUID quizId) {
        return userResponseRepository.findByQuizId(quizId);
    }

    public UserResponseEntity getResponse (UUID responseId) throws BadRequestException {
        return userResponseRepository.findById(responseId)
                .orElseThrow(() -> new BadRequestException("Response not found with ID: " + responseId));
    }

    @Override
    public QuestionAnswerEntity addAnswer(UUID responseId, UUID questionId, String answer) throws BadRequestException {
        UserResponseEntity response = getResponse(responseId);
        QuestionEntity question = questionService.getQuestionById(questionId);

        questionService.checkIfQuestionBelongsToQuiz(response.getQuiz().getId(), questionId);
        checkAnswer(question, answer);
        Boolean isCorrect = question.getCorrectAnswer().equals(answer);

        QuestionAnswerEntity existingAnswer = answerRepository.findByResponseAndQuestion(response, question);

        if (existingAnswer != null) {
            existingAnswer.setAnswer(answer);
            existingAnswer.setIsCorrect(isCorrect);
            return answerRepository.save(existingAnswer);
        }

        QuestionAnswerEntity questionAnswer = new QuestionAnswerEntity();
        questionAnswer.setAnswer(answer);
        questionAnswer.setResponse(response);
        questionAnswer.setQuestion(question);
        questionAnswer.setIsCorrect(isCorrect);

        return answerRepository.save(questionAnswer);
    }

    private void checkAnswer (QuestionEntity question, String answer) throws BadRequestException {
        if (!question.getVariants().contains(answer)) {
            throw new BadRequestException("Answer must be one of the variants.");
        }
    }
}
