package com.example.lab_7.service;

import com.example.lab_7.dto.Session.SessionResponseDTO;
import com.example.lab_7.dto.User.UserRequestDTO;
import com.example.lab_7.dto.User.UserResponseDTO;
import com.example.lab_7.dto.User.UserUpdateDTO;
import com.example.lab_7.model.FullUser;
import com.example.lab_7.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    public UserService(UserRepository userRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        userRepository.createTable();
        this.sessionService = sessionService;
    }
    private UserResponseDTO toDto(FullUser user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getBiography()
        );
    }
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        FullUser user = new FullUser();
        user.setName(userRequestDTO.getName());
        user.setPassword(new Argon2PasswordEncoder(32, 64, 1, 15 * 1024, 2).encode(userRequestDTO.getPassword()));
        if (userRequestDTO.getBiography()==null){
            user.setBiography("None");
        }else {
            user.setBiography(userRequestDTO.getBiography());
        }
        return toDto(userRepository.insertUser(user));
    }
    private Boolean doesPassMatch(String password, String hashedPassword){
        return new Argon2PasswordEncoder(32, 64, 1, 15 * 1024, 2).matches(password,hashedPassword);
    }
    public UserResponseDTO loginUser(UserRequestDTO userRequestDTO){
        FullUser selectedUser = selectUserByName(userRequestDTO.getName());
        if (doesPassMatch(userRequestDTO.getPassword(), selectedUser.getPassword())){
            return toDto(selectedUser);
        }else{
            return null;
        }
    }
    public UserResponseDTO selectUserById(Long id){
        return toDto(userRepository.getUserById(id));
    }
    public FullUser selectUserByName(String name){
        return userRepository.getUserByName(name);
    }
    public UserResponseDTO selectUserByToken(String token){
        SessionResponseDTO session = sessionService.getSessionByToken(token);
        if (session != null) {
            String sessionId = session.getId();
            Long userId = session.getUserId();
            Long expiresIn = session.getExpiresIn();
            long currentTime = System.currentTimeMillis();
            if (currentTime > expiresIn) {
                sessionService.deleteSession(sessionId);
                return null;
            }
            return selectUserById(userId);
        }
        return null;
    }
    public List<UserResponseDTO> selectAllUsers(Integer page, Integer size){
        return userRepository.getAllUsers(page, size).stream().map(this::toDto).collect(Collectors.toList());
    }
    public void updateUser(Long id, UserUpdateDTO userUpdateDTO){
        FullUser user = new FullUser();
        user.setName(userUpdateDTO.getName());
        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(new Argon2PasswordEncoder(32, 64, 1, 15 * 1024, 2).encode(userUpdateDTO.getPassword()));
        }else{
            user.setPassword(null);
        }
        user.setBiography(userUpdateDTO.getBiography());
        user.setId(id);
        userRepository.updateUser(user);
    }
    public void deleteUser(Long id){
        userRepository.deleteUser(id);
    }
}
