package com.example.lab_7.controller;

import com.example.lab_7.dto.Session.SessionRequestDTO;
import com.example.lab_7.dto.Session.SessionResponseDTO;
import com.example.lab_7.dto.User.UserRequestDTO;
import com.example.lab_7.dto.User.UserResponseDTO;
import com.example.lab_7.dto.User.UserUpdateDTO;
import com.example.lab_7.service.CookieService;
import com.example.lab_7.service.SessionService;
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
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;
    private final CookieService cookieService;
    public UserController(UserService userService, SessionService sessionService, CookieService cookieService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
    }
    @GetMapping({"/",""})
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(@RequestParam(value="p", required = false, defaultValue = "0") Integer page, @RequestParam(value="s", required = false, defaultValue = "20") Integer size) {
        List<UserResponseDTO> users = userService.selectAllUsers(page, size);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(users);
    }
    @GetMapping({"/{id}","/{id}/"})
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long id) {
        UserResponseDTO user = userService.selectUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }
    @PostMapping({"/",""})
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO user, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> cookieMap = cookieService.getCookie(request.getCookies());
        String token = (String) cookieMap.get("token");
        Cookie spec_cookie = (Cookie) cookieMap.get("spec_cookie");
        UserResponseDTO createdUser;
        if (user.getRegister()!=null && user.getRegister()) {
            createdUser = userService.createUser(user);
            if (createdUser == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }else{
            createdUser = userService.loginUser(user);
            if (createdUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        if (token != null) {

            SessionResponseDTO existingSession = sessionService.getSessionByToken(token);
            if (existingSession != null) {
                sessionService.deleteSession(existingSession.getId());
                response.addCookie(cookieService.deleteCookie(spec_cookie));
            }
        }

        SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();
        sessionRequestDTO.setUserId(createdUser.getId());
        SessionResponseDTO sessionResponseDTO = sessionService.insertSession(sessionRequestDTO);
        if (sessionResponseDTO != null) {
            response.addCookie(cookieService.createCookie(sessionResponseDTO));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    @PutMapping({"/{id}","/{id}/"})
    public ResponseEntity<UserResponseDTO> putUser(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateDTO user, HttpServletRequest request, HttpServletResponse response) {
        return updateUser(id, user, request, response);
    }
    @PatchMapping({"/{id}","/{id}/"})
    public ResponseEntity<UserResponseDTO> patchUser(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateDTO user, HttpServletRequest request, HttpServletResponse response) {
        return updateUser(id, user, request, response);
    }

    private ResponseEntity<UserResponseDTO> updateUser(@PathVariable("id") Long id, @RequestBody @Valid UserUpdateDTO user, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> cookieMap = cookieService.getCookie(request.getCookies());
        String token = (String) cookieMap.get("token");
        Cookie spec_cookie = (Cookie) cookieMap.get("spec_cookie");
        if (token!=null){
            UserResponseDTO userResponseDTO = userService.selectUserByToken(token);
            if (userResponseDTO == null) {
                response.addCookie(cookieService.deleteCookie(spec_cookie));
            }else{
                if (id.equals(userResponseDTO.getId())) {
                    userService.updateUser(id, user);
                    return ResponseEntity.ok(userService.selectUserById(id));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping({"/{id}","/{id}/"})
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> cookieMap = cookieService.getCookie(request.getCookies());
        String token = (String) cookieMap.get("token");
        Cookie spec_cookie = (Cookie) cookieMap.get("spec_cookie");
        if (token!=null){
            UserResponseDTO userResponseDTO = userService.selectUserByToken(token);
            if (userResponseDTO == null) {
                response.addCookie(cookieService.deleteCookie(spec_cookie));
            }else{
                if (id.equals(userResponseDTO.getId())) {
                    sessionService.deleteAllSessions(id);
                    userService.deleteUser(id);
                    response.addCookie(cookieService.deleteCookie(spec_cookie));
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
