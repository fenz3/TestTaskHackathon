package com.example.itquizletspringbootapi.repository;

import com.example.itquizletspringbootapi.repository.entity.SavedQuizEntity;
import com.example.itquizletspringbootapi.repository.entity.SavedQuizId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedQuizzes extends JpaRepository<SavedQuizEntity, SavedQuizId> {
    List<SavedQuizEntity> findAllByIdUserId(UUID userId);
}