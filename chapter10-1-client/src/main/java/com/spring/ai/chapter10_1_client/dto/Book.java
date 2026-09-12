package com.spring.ai.chapter10_1_client.dto;

public class Book {
    private Long id;
    private String title;
    private String author;
    private int year;

    // 직접 코드를 치기보다 인텔리제이의 [Generate] 기능을 사용해
    // Getter와 Setter를 자동으로 생성하면 오타를 줄일 수 있습니다.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}