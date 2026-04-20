package com.example.library.exception;

public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(Long bookId) {
        super("Book with id " + bookId + " is already borrowed and has not been returned.");
    }
}
