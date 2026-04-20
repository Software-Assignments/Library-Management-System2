package com.example.library.controller;

import com.example.library.dto.request.BorrowRecordRequestDto;
import com.example.library.dto.response.BorrowRecordResponseDto;
import com.example.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @PostMapping("/api/borrow-records")
    public ResponseEntity<BorrowRecordResponseDto> borrowBook(
            @Valid @RequestBody BorrowRecordRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(borrowRecordService.borrowBook(dto));
    }

    @PutMapping("/api/borrow-records/{id}/return")
    public ResponseEntity<BorrowRecordResponseDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }

    // Handles both /api/borrow-records/member/{memberId} and
    // /api/borrowrecords/member/{memberId} (spec uses no-hyphen variant)
    @GetMapping({"/api/borrow-records/member/{memberId}",
                 "/api/borrowrecords/member/{memberId}",
                 "/api/borrow-records/members/{memberId}"})
    public ResponseEntity<List<BorrowRecordResponseDto>> getRecordsByMember(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(borrowRecordService.getRecordsByMember(memberId));
    }

    @GetMapping("/api/borrow-records/active")
    public ResponseEntity<List<BorrowRecordResponseDto>> getActiveRecords() {
        return ResponseEntity.ok(borrowRecordService.getActiveRecords());
    }
}
