package com.example.lab_7.service;

import com.example.lab_7.dto.Session.SessionResponseDTO;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CookieService {
    public Cookie createCookie(SessionResponseDTO session) {
        Cookie cookie = new Cookie("JWT", session.getToken());
        cookie.setMaxAge(7*24*60*60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
    public Map<String,Object> getCookie(Cookie[] cookies){
        String token=null;
        Cookie spec_cookie=null;
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("JWT")){
                    token = cookie.getValue();
                    spec_cookie = cookie;
                }
            }
        }
        Map<String,Object> cookieMap = new HashMap<>();
        cookieMap.put("token",token);
        cookieMap.put("spec_cookie",spec_cookie);
        return cookieMap;
    }
    public Cookie deleteCookie(Cookie spec_cookie){
        spec_cookie.setValue(null);
        spec_cookie.setMaxAge(0);
        spec_cookie.setPath("/");
        return spec_cookie;
    }
}
