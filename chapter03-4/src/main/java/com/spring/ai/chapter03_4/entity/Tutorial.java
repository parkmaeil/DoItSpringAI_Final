package com.spring.ai.chapter03_4.entity;

public class Tutorial {
    private String title;
    private String content;
    private int createdYear;

    public Tutorial() {
    }

    public Tutorial(String title, int createdYear, String content) {
        this.title = title;
        this.createdYear = createdYear;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreatedYear(int createdYear) {
        this.createdYear = createdYear;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getCreatedYear() {
        return createdYear;
    }
}