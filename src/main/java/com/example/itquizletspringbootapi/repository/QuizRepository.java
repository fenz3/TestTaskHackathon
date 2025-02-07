package com.example.itquizletspringbootapi.repository;

import com.example.itquizletspringbootapi.repository.entity.Level;
import com.example.itquizletspringbootapi.repository.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
    List<QuizEntity> findByOwnerId(UUID ownerId);

    @Query("SELECT q FROM QuizEntity q WHERE " +
            "(:category IS NULL OR LOWER(q.categories) LIKE LOWER(CONCAT('%', :category, '%'))) " +
            "AND (:level IS NULL OR q.level = :level)")
    List<QuizEntity> findByCategoryAndLevel(@Param("category") String category, @Param("level") Level level);

}
