package com.example.library.service;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> findAllBooks(Pageable pageable) {
        return bookRepository.findAllWithAuthor(pageable).map(bookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return bookMapper.toResponse(bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id)));
    }

    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn()))
            throw new DuplicateResourceException("Book with ISBN '" + request.getIsbn() + "' already exists");
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", request.getAuthorId()));
        Book book = bookMapper.toEntity(request);
        book.setAuthor(author);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn()))
            throw new DuplicateResourceException("Book with ISBN '" + request.getIsbn() + "' already exists");
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", request.getAuthorId()));
        bookMapper.updateFromRequest(request, book);
        book.setAuthor(author);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id))
            throw new ResourceNotFoundException("Book", id);
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> search(String title, String genre, Integer publishedYear) {
        return bookRepository.searchBooks(title, genre, publishedYear)
                .stream().map(bookMapper::toResponse).toList();
    }
}