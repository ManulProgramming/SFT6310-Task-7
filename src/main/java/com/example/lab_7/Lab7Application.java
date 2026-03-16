package com.example.lab_7;

import com.example.lab_7.repository.PostRepository;
import com.example.lab_7.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@Service
class AllServices {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public AllServices(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        userRepository.createTable();
        this.postRepository = postRepository;
        postRepository.createTable();
    }
}

@SpringBootApplication()
public class Lab7Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab7Application.class, args);
    }

}
