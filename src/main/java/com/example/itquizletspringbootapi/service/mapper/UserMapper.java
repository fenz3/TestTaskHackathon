package com.example.itquizletspringbootapi.service.mapper;

import com.example.itquizletspringbootapi.dto.user.UserDto;
import com.example.itquizletspringbootapi.dto.user.UserRegisterDto;
import com.example.itquizletspringbootapi.dto.user.UserUpdateDto;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDto dto);

    UserEntity toEntity(UserRegisterDto dto);

    UserDto toDto(UserEntity entity);

    UserEntity updateUserFromDto(UserUpdateDto dto, @MappingTarget UserEntity existingEntity);
}
