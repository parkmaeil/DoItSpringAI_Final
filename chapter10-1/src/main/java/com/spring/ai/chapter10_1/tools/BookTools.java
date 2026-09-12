package com.spring.ai.chapter10_1.tools;

import com.spring.ai.chapter10_1.entity.Book;
import com.spring.ai.chapter10_1.repository.BookRepository;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookTools {
    private final BookRepository bookRepository;

    public BookTools(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @McpTool(name = "get_all_books", description = "모든 책 목록을 가져옵니다.")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    @McpTool(name = "search_books_by_author", description = "저자 이름으로 책을 검색합니다.")
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    @McpTool(name = "get_book_by_title", description = "정확한 제목으로 단일 책을 가져옵니다.")
    public Book getBookByTitle(String title) {
        return bookRepository.findByTitle(title).orElse(null);
    }
}
