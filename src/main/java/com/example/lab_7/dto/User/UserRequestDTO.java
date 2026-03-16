package com.example.lab_7.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRequestDTO {
    @Pattern(regexp = "^[a-zA-Z0-9._-]{1,50}$", message = "Invalid username. Username can only contain lowercase and uppercase characters, dot, underscore and dash and be no more than 50 characters long")
    @NotBlank
    private String name;
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "Invalid password. Password requires at least 8 characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character")
    @NotBlank
    private String password;
    @Pattern(regexp = "^.{0,2500}$", message = "Invalid biography. Biography cannot be more than 2500 characters long")
    private String biography;
    private Boolean register;
    public UserRequestDTO(String name, String password, String biography, Boolean register) {
        this.name = name;
        this.password = password;
        this.biography = biography;
        this.register = register;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }
    public Boolean getRegister() {
        return register;
    }
    public void setRegister(Boolean register) {
        this.register = register;
    }
}
