package com.example.library.mapper;

import com.example.library.dto.response.BookResponse;
import com.example.library.entity.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class})
public interface BookMapper {

    BookResponse toResponse(Book book);

}
