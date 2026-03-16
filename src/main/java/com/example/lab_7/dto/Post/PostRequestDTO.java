package com.example.lab_7.dto.Post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PostRequestDTO {
    @Min(1)
    private Long userId;
    @Pattern(regexp = "^.{1,100}$", message = "Invalid title. Title should not be empty and be less than 100 characters long")
    @NotBlank
    private String title;
    @Pattern(regexp = ".{1,2500}", message = "Invalid content. Content cannot be empty or more than 2500 characters long")
    @NotBlank
    private String content;
    public PostRequestDTO(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
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
