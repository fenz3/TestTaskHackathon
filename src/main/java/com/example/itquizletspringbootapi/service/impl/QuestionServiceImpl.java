package com.example.itquizletspringbootapi.service.impl;

import com.example.itquizletspringbootapi.dto.question.QuestionCreateDto;
import com.example.itquizletspringbootapi.dto.question.QuestionData;
import com.example.itquizletspringbootapi.dto.question.QuestionUpdateDto;
import com.example.itquizletspringbootapi.repository.QuestionRepository;
import com.example.itquizletspringbootapi.repository.entity.QuestionEntity;
import com.example.itquizletspringbootapi.repository.entity.QuizEntity;
import com.example.itquizletspringbootapi.service.QuestionService;
import com.example.itquizletspringbootapi.service.mapper.QuestionMapper;
import com.example.itquizletspringbootapi.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuestionMapper questionMapper;

    public void checkIfQuestionBelongsToQuiz(UUID quizId, UUID questionId) throws BadRequestException {
        QuestionEntity question = questionRepository.findById(questionId).
                orElseThrow(() -> new BadRequestException("Question not found with ID: " + questionId));

        if (!question.getQuiz().getId().equals(quizId)) {
            throw new BadRequestException("The question does not belong to the specified quiz.");
        }
    }

    public void checkAnswer(QuestionData question) throws BadRequestException {
        if (!question.getVariants().contains(question.getCorrectAnswer())) {
            throw new BadRequestException("Correct answer must be one of the variants.");
        }
    }

    @Override
    public QuestionEntity addQuestionToQuiz(UUID quizId, QuestionCreateDto question) throws BadRequestException {
        checkAnswer(question);

        QuizEntity quizEntity = quizRepository.findById(quizId)
                .orElseThrow(() -> new BadRequestException("Quiz not found with ID: " + quizId));

        QuestionEntity questionEntity = questionMapper.toEntity(question);
        questionEntity.setQuiz(quizEntity);
        return questionRepository.save(questionEntity);
    }

    @Override
    public QuestionEntity getQuestionById(UUID questionId) throws BadRequestException {
        return questionRepository.findById(questionId)
                        .orElseThrow(() -> new BadRequestException("Questions not found with ID: " + questionId));
    }

    @Override
    public List<QuestionEntity> getQuestionsByQuiz(UUID quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Override
    @Transactional
    public QuestionEntity updateQuestion(UUID questionId, QuestionUpdateDto updatedQuestionDto) throws BadRequestException {
        QuestionEntity questionEntity = getQuestionById(questionId);
        questionMapper.updateEntityFromDto(updatedQuestionDto, questionEntity);

        checkAnswer(questionEntity);
        return questionRepository.save(questionEntity);
    }

    @Override
    public void deleteQuestion(UUID questionId) {
        questionRepository.deleteById(questionId);
    }

}
