package com.spring.ai.chapter10_1.repository;

import com.spring.ai.chapter10_1.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
}
