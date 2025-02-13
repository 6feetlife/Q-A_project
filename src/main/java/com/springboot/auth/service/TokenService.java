package com.springboot.auth.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    // 토큰에서 사용자 email 추출
    public String getUserIdFromToken(String token) {
        // 받아온 토큰에서 "Bearer " 제거
        String pureToken = token.replace("Bearer ", "");

        // payload 추출
        Claims claims = Jwts.parser()
                .parseClaimsJws(pureToken)
                .getBody();

        // email 반환
        return claims.getSubject();
    }


}
