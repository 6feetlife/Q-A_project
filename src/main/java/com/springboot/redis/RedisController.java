package com.springboot.redis;

import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/v11/auth")
@RequiredArgsConstructor
public class RedisController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenizer jwtTokenizer;
    private final RedisService redisService;

    @PostMapping("/logout")
    public ResponseEntity postLogout(Authentication authentication, HttpServletResponse response) {
        String username = authentication.getName(); // 현재 인증된 사용자의 사용자명을 가져옵니다.

        if (redisService.logout(username)) {
            // 🔹 RefreshToken 쿠키 삭제 (만료 시간 0 설정)
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(false)
                    .domain("localhost") // 프론트엔드 도메인에 맞게 변경
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(0) // 즉시 만료
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
