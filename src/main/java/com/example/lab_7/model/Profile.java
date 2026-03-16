package com.example.lab_7.model;

public class Profile {
    private Long id;
    private String biography;
    public Profile(Long id, Long userId, String biography) {
        this.id = id;
        this.biography = biography;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }
}
