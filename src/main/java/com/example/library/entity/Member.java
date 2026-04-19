package com.example.library.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    @Column(updatable = false)
    private LocalDate membershipDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (membershipDate == null) {
            membershipDate = LocalDate.now();
        }
    }
}