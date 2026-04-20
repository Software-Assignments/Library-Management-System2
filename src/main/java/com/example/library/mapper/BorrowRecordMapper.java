package com.example.library.mapper;

import com.example.library.dto.request.BorrowRecordRequestDto;
import com.example.library.dto.response.BorrowRecordResponseDto;
import com.example.library.entity.BorrowRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BorrowRecordMapper {

    @Mapping(target = "book.id", source = "bookId")
    @Mapping(target = "member.id", source = "memberId")
    @Mapping(target = "borrowDate", ignore = true)
    @Mapping(target = "returnDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    BorrowRecord toEntity(BorrowRecordRequestDto dto);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.firstName", target = "memberFirstName")
    @Mapping(source = "member.lastName", target = "memberLastName")
    BorrowRecordResponseDto toDto(BorrowRecord entity);
}
