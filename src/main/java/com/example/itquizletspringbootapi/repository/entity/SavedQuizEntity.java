package com.example.itquizletspringbootapi.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "saved_quizzes")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SavedQuizEntity {
    @EmbeddedId
    private SavedQuizId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private UserEntity user;

    @ManyToOne
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false, nullable = false)
    private QuizEntity quiz;
}
