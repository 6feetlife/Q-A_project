package com.springboot.redis;

import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity postLogout(Authentication authentication) {
        String username = authentication.getName(); // 현재 인증된 사용자의 사용자명을 가져옵니다.

        // AuthService의 logout 메서드를 호출하여 로그아웃을 처리하고, 결과에 따라 HTTP 상태 코드를 반환합니다.
        return redisService.logout(username) ?
                new ResponseEntity(HttpStatus.OK) : new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
