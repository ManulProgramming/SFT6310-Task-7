package com.example.lab_7.controller;

import com.example.lab_7.dto.Post.PostResponseDTO;
import com.example.lab_7.dto.Post.PostRequestDTO;
import com.example.lab_7.dto.Post.PostUpdateDTO;
import com.example.lab_7.dto.User.UserResponseDTO;
import com.example.lab_7.service.CookieService;
import com.example.lab_7.service.PostService;
import com.example.lab_7.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final CookieService cookieService;
    private final UserService userService;
    public PostController(PostService postService, CookieService cookieService, UserService userService) {
        this.postService = postService;
        this.cookieService = cookieService;
        this.userService = userService;
    }
    @GetMapping({"/",""})
    public ResponseEntity<List<PostResponseDTO>> getPosts(@RequestParam(value="p", required = false, defaultValue = "0") Integer page, @RequestParam(value="s", required = false, defaultValue = "20") Integer size) {
        List<PostResponseDTO> posts = postService.getAll(page, size);
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(posts);
    }
    @GetMapping({"/{id}","/{id}/"})
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable("id") Long id) {
        PostResponseDTO post = postService.getById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(post);
    }
    @PostMapping({"/",""})
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO post, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> cookieMap = cookieService.getCookie(request.getCookies());
        String token = (String) cookieMap.get("token");
        Cookie spec_cookie = (Cookie) cookieMap.get("spec_cookie");
        if (token!=null) {
            UserResponseDTO user = userService.selectUserByToken(token);
            if (user == null) {
                response.addCookie(cookieService.deleteCookie(spec_cookie));
            }else{
                post.setUserId(user.getId());
                PostResponseDTO createdPost = postService.create(post);
                if (createdPost == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @PutMapping({"/{id}","/{id}/"})
    public ResponseEntity<PostResponseDTO> putPost(@PathVariable("id") Long id, @Valid @RequestBody PostUpdateDTO post, HttpServletRequest request, HttpServletResponse response) {
        return updatePost(id, post, request, response);
    }
    @PatchMapping({"/{id}","/{id}/"})
    public ResponseEntity<PostResponseDTO> patchPost(@PathVariable("id") Long id, @Valid @RequestBody PostUpdateDTO post, HttpServletRequest request, HttpServletResponse response) {
        return updatePost(id, post, request, response);
    }

    private ResponseEntity<PostResponseDTO> updatePost(@PathVariable("id") Long id, @RequestBody @Valid PostUpdateDTO post, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> cookieMap = cookieService.getCookie(request.getCookies());
        String token = (String) cookieMap.get("token");
        Cookie spec_cookie = (Cookie) cookieMap.get("spec_cookie");
        if (token!=null){
            UserResponseDTO user = userService.selectUserByToken(token);
            if (user == null) {
                response.addCookie(cookieService.deleteCookie(spec_cookie));
            }else{
                PostResponseDTO postResponseDTO = postService.getById(id);
                if (postResponseDTO != null && postResponseDTO.getUserId().equals(user.getId())){
                    postService.update(id, post);
                    return ResponseEntity.ok(postService.getById(id));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping({"/{id}","/{id}/"})
    public ResponseEntity<PostResponseDTO> deletePost(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> cookieMap = cookieService.getCookie(request.getCookies());
        String token = (String) cookieMap.get("token");
        Cookie spec_cookie = (Cookie) cookieMap.get("spec_cookie");
        if (token!=null){
            UserResponseDTO user = userService.selectUserByToken(token);
            if (user == null) {
                response.addCookie(cookieService.deleteCookie(spec_cookie));
            }else{
                PostResponseDTO postResponseDTO = postService.getById(id);
                if (postResponseDTO != null && postResponseDTO.getUserId().equals(user.getId())){
                    postService.delete(id);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
