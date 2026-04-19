package com.example.library.mapper;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.entity.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class})
public interface BookMapper {

    @Mapping(target = "author", ignore = true)
    Book toEntity(BookRequest request);

    BookResponse toResponse(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", ignore = true)
    void updateFromRequest(BookRequest request, @MappingTarget Book book);
}
