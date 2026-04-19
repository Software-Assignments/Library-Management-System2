package com.example.library.mapper;

import com.example.library.dto.request.AuthorRequest;
import com.example.library.dto.response.AuthorResponse;
import com.example.library.entity.Author;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorRequest request);
    AuthorResponse toResponse(Author author);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(AuthorRequest request, @MappingTarget Author author);
}
