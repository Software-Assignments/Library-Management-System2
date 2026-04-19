package com.example.library.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private String genre;
    private Integer publishedYear;
    private AuthorResponse author;
}
