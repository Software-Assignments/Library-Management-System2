package com.example.library.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BorrowRecordResponseDto {
    private Long id;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private Long bookId;
    private String bookTitle;
    private Long memberId;
    private String memberFirstName;
    private String memberLastName;
}
