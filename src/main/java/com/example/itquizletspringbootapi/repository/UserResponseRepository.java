package com.example.itquizletspringbootapi.repository;

import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.repository.entity.UserResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserResponseRepository extends JpaRepository<UserResponseEntity, UUID> {
    List<UserResponseEntity> findByQuizId(UUID quizId);

    List<UserResponseEntity> findByUser(UserEntity user);
}
