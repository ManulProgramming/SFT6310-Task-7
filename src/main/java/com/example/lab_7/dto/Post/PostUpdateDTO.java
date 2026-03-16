package com.example.lab_7.dto.Post;

import jakarta.validation.constraints.Pattern;

public class PostUpdateDTO {
    @Pattern(regexp = "^.{0,100}$", message = "Invalid title. Title should not be empty and be less than 100 characters long")
    private String title;
    @Pattern(regexp = ".{0,2500}", message = "Invalid content. Content cannot be empty or more than 2500 characters long")
    private String content;
    public PostUpdateDTO(Long userId, String title, String content) {
        this.title = title;
        this.content = content;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
