package com.example.library.service;

import com.example.library.dto.request.BorrowRecordRequestDto;
import com.example.library.dto.response.BorrowRecordResponseDto;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Member;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordMapper borrowRecordMapper;

    public BorrowRecordResponseDto borrowBook(BorrowRecordRequestDto dto) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", dto.getBookId()));

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", dto.getMemberId()));

        borrowRecordRepository.findByBookIdAndReturnDateIsNull(dto.getBookId())
                .ifPresent(r -> {
                    throw new DuplicateResourceException(
                            "Book with id " + dto.getBookId() + " is already borrowed and has not been returned.");
                });

        BorrowRecord record = BorrowRecord.builder()
                .book(book)
                .member(member)
                .build();

        BorrowRecord saved = borrowRecordRepository.save(record);
        return borrowRecordMapper.toDto(saved);
    }

    public BorrowRecordResponseDto returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", id));

        if (record.getReturnDate() != null) {
            throw new IllegalStateException("This borrow record has already been returned.");
        }

        record.setReturnDate(LocalDate.now());
        BorrowRecord saved = borrowRecordRepository.save(record);
        return borrowRecordMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getRecordsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member", "id", memberId);
        }
        return borrowRecordRepository.findByMemberId(memberId)
                .stream().map(borrowRecordMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getActiveRecords() {
        return borrowRecordRepository.findByReturnDateIsNull()
                .stream().map(borrowRecordMapper::toDto).toList();
    }
}
