package com.example.library.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationality;
    private LocalDate birthDate;
}
