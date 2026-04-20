package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @EntityGraph(attributePaths = {"book", "member"})
    List<BorrowRecord> findByMemberId(Long memberId);

    @EntityGraph(attributePaths = {"book", "member"})
    List<BorrowRecord> findByReturnDateIsNull();

    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);
}
