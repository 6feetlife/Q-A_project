package com.springboot.auth.service;

import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class TokenService {
    private final JwtTokenizer jwtTokenizer;

    public TokenService(JwtTokenizer jwtTokenizer) {
        this.jwtTokenizer = jwtTokenizer;
    }


    @Value("${jwt.key}")
    private String secretKey;

    // 토큰에서 사용자 email 추출
    public String getUserIdFromToken(String token) {
        // 받아온 토큰에서 "Bearer " 제거
        String pureToken = token.replace("Bearer ", "");

        // payload 추출
        Claims claims = Jwts.parser()
                .setSigningKey(jwtTokenizer.encodeBase64SecretKey(secretKey))
                .parseClaimsJws(pureToken)
                .getBody();

        // email 반환
        return claims.getSubject();
    }


}
