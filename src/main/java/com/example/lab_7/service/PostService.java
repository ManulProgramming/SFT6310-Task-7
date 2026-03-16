package com.example.lab_7.service;

import com.example.lab_7.dto.Post.PostRequestDTO;
import com.example.lab_7.dto.Post.PostResponseDTO;
import com.example.lab_7.dto.Post.PostUpdateDTO;
import com.example.lab_7.model.Post;
import com.example.lab_7.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
        postRepository.createTable();
    }
    private PostResponseDTO toDto(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent()
        );
    }
    public List<PostResponseDTO> getAll(Integer page, Integer size) {
        return postRepository.getAllPosts(page, size).stream().map(this::toDto).collect(Collectors.toList());
    }
    public PostResponseDTO getById(Long id) {
        return toDto(postRepository.getPostById(id));
    }
    public PostResponseDTO create(PostRequestDTO postRequestDTO) {
        Post post = new Post();
        post.setUserId(postRequestDTO.getUserId());
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        return toDto(postRepository.insertPost(post));
    }
    public void update(Long id, PostUpdateDTO postUpdateDTO) {
        Post post = new Post();
        post.setTitle(postUpdateDTO.getTitle());
        post.setContent(postUpdateDTO.getContent());
        post.setId(id);
        postRepository.updatePost(post);
    }
    public void delete(Long id) {
        postRepository.deletePost(id);
    }
}
