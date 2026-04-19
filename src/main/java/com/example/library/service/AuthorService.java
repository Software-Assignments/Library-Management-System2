package com.example.library.service;

import com.example.library.dto.request.AuthorRequest;
import com.example.library.dto.response.AuthorResponse;
import com.example.library.dto.response.BookResponse;
import com.example.library.entity.Author;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.AuthorMapper;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public Page<AuthorResponse> findAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable).map(authorMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        return authorMapper.toResponse(getOrThrow(id));
    }

    public AuthorResponse create(AuthorRequest request) {
        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = getOrThrow(id);
        authorMapper.updateFromRequest(request, author);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByAuthor(Long authorId) {
        getOrThrow(authorId);
        return bookRepository.findByAuthorIdWithAuthor(authorId)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    private Author getOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }
}