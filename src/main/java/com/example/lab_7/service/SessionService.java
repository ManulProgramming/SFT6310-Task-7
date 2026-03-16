package com.example.lab_7.service;

import com.example.lab_7.dto.Session.SessionRequestDTO;
import com.example.lab_7.dto.Session.SessionResponseDTO;
import com.example.lab_7.model.Session;
import com.example.lab_7.repository.MongoSessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final MongoSessionRepository sessionRepository;
    private final JwtService jwtService;
    public SessionService(MongoSessionRepository sessionRepository, JwtService jwtService) {
        this.sessionRepository = sessionRepository;
        this.jwtService = jwtService;
    }
    private SessionResponseDTO toDto(Session session) {
        SessionResponseDTO dto = new SessionResponseDTO();
        dto.setId(session.getId());
        dto.setToken(session.getToken());
        dto.setUserId(session.getUserId());
        dto.setExpiresIn(session.getExpiresIn());
        return dto;
    }
    public SessionResponseDTO insertSession(@Valid SessionRequestDTO sessionRequestDTO) {
        Session session = new Session();

        session.setUserId(sessionRequestDTO.getUserId());
        session.setExpiresIn(System.currentTimeMillis()+7*24*60*60*1000);

        Session createdSession = sessionRepository.insertSession(session);

        String jwt = jwtService.generateToken(createdSession.getId(),createdSession.getUserId(),7*24*60*60*1000);

        createdSession.setToken(jwt);

        return toDto(createdSession);
    }
    public SessionResponseDTO getSessionByToken(String token) {
        try{
            Claims claims = jwtService.parseToken(token);
            String sessionId = claims.get("sid", String.class);
            Session session = sessionRepository.getSessionById(sessionId);
            if (session == null) {
                return null;
            }
            return toDto(session);
        }catch (Exception e){
            return null;
        }
    }
    public void deleteSession(String id) {
        sessionRepository.deleteSession(id);
    }
    public void deleteAllSessions(Long userId) {
        sessionRepository.deleteAllSessionsForUserId(userId);
    }
}
