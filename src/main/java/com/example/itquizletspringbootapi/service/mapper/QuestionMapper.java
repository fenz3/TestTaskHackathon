package com.example.itquizletspringbootapi.service.mapper;

import com.example.itquizletspringbootapi.repository.entity.QuestionEntity;
import com.example.itquizletspringbootapi.dto.question.QuestionDto;
import com.example.itquizletspringbootapi.dto.question.QuestionCreateDto;
import com.example.itquizletspringbootapi.dto.question.QuestionUpdateDto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionEntity toEntity(QuestionDto dto);

    QuestionEntity toEntity(QuestionCreateDto dto);

    QuestionDto toDTO(QuestionEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    QuestionEntity updateEntityFromDto(QuestionUpdateDto updateDto, @MappingTarget QuestionEntity existingEntity);
}
