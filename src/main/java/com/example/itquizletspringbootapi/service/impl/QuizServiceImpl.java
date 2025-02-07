package com.example.itquizletspringbootapi.service.impl;

import com.example.itquizletspringbootapi.dto.quiz.QuizCreateDto;
import com.example.itquizletspringbootapi.dto.quiz.QuizDto;
import com.example.itquizletspringbootapi.dto.quiz.QuizUpdateDto;
import com.example.itquizletspringbootapi.repository.QuizRepository;
import com.example.itquizletspringbootapi.repository.entity.Level;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.service.QuizService;
import com.example.itquizletspringbootapi.service.mapper.QuizMapper;
import com.example.itquizletspringbootapi.repository.entity.QuizEntity;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    public QuizEntity createQuiz(QuizCreateDto quiz, UserEntity owner) {
        System.out.println(quiz.getCategories());
        QuizEntity quizEntity = QuizEntity.builder()
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .level(quiz.getLevel())
                .categories(quiz.getCategories())
                .owner(owner)
                .build();

        return quizRepository.save(quizEntity);
    }

    @Override
    public QuizEntity getQuizById(UUID quizId) throws BadRequestException {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new BadRequestException("Quiz not found with ID: " + quizId));
    }

    @Override
    public List<QuizDto> getAllQuizzes(Level level, Optional<String> category){
        List<QuizEntity> quizzes = quizRepository.findByCategoryAndLevel(category.orElse(""), level);
        return quizzes.stream()
                .map(quizMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizEntity> getQuizzesByOwner(UUID ownerId) {
        return quizRepository.findByOwnerId(ownerId);
    }

    @Override
    public QuizEntity updateQuiz(UUID quizId, QuizUpdateDto updatedQuizDto, UUID userId) throws BadRequestException {
        QuizEntity quizEntity = quizRepository.findById(quizId)
                .orElseThrow(() -> new BadRequestException("Quiz not found with ID: " + quizId));

        this.checkOwner(quizId, userId);
        quizMapper.updateEntityFromDto(updatedQuizDto, quizEntity);

        return quizRepository.save(quizEntity);
    }

    @Override
    public void deleteQuiz(UUID quizId, UUID userId) throws BadRequestException {
        this.checkOwner(quizId, userId);

        quizRepository.deleteById(quizId);
    }

    public void checkOwner (UUID quizId, UUID ownerId) throws BadRequestException {
        QuizEntity quizEntity = quizRepository.findById(quizId)
                .orElseThrow(() -> new BadRequestException("Quiz not found with ID: " + quizId));
        if (!quizEntity.getOwner().getId().equals(ownerId) ) {
            throw new BadRequestException("You are not the owner of this quiz.");
        }
    }
}
