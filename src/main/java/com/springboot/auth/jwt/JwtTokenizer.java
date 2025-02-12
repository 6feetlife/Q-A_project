package com.springboot.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenizer {

    @Getter
    // yml 에서 key 값 가져옴
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    // yml 에서 access 토큰의 만료 기간 가져옴
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    // yml 에서 refresh 토큰의 만료 기간 가져옴
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    // 입력받은 비밀번호를 인코딩한다.
    public String encodeBase64SecretKey(String secretKey) {
        // secretKey 문자열을 UTF-8바이트 배열로 변환하여 인코딩
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodedSecretKey) {
        // 인코딩된 비밀번호로 서명키 생성
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        return Jwts.builder()
                // 민감한 정보
                .setClaims(claims)
                // payload 정보
                .setSubject(subject)
                // 발급시간을 의미하며, payload 부분에 iat 필드로 저장됨
                .setIssuedAt(Calendar.getInstance().getTime())
                // 만료기간
                .setExpiration(expiration)
                // 위변조 되지 않았다는 서명이 들어감
                .signWith(key)
                // 압축
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(String subject,
                                       Date expiration,
                                       String base64EncodedSecretKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
        // accessToken 재발급에 사용되는 토큰으로 claims 는 담지 않는다.
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();

    }

    // 서명 검증을 통과한 JWT 내부정보(Claims)를 검증 후 반환하는 역할
    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey) {
        // 인코딩된 비밀번호로 서명키 생성
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        // Jwts.parserBuilder() = JWT 를 해석할 Parser 객체 생성
        Jws<Claims> claims = Jwts.parserBuilder()
                // 서명키 설정
                .setSigningKey(key)
                .build()
                // jws 를 파싱하고 내부 정보를 해석해서 서명이 유효하지 않거나 만료된 경우 예외를 발생시킨다.
                .parseClaimsJws(jws);
        return claims;
    }

    // 서명 유효성 검증
    public void verifySignature(String jws, String base64EncodedSecretKey) {
        // 시크릿키 디코딩
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
        Jwts.parserBuilder()
                // 서명키 설정
                .setSigningKey(key)
                .build()
                // 서명 유효성 검증, 유효하지 않다면 예외 발생
                .parseClaimsJws(jws);
    }

    // 토큰 만료기한 지정
    public Date getTokenExpiration(int expirationMinutes) {
        // 현재 시스템의 시간과 날짜 정보를 담은 객체 생성
        Calendar calendar = Calendar.getInstance();
        // 생성한 날짜 객체에 만료 기한 설정
        calendar.add(Calendar.MINUTE,expirationMinutes);
        // Calendar 객체에서 현재 날짜와 시간 정보를 Date 객체로 반환
        Date expiration = calendar.getTime();

        // 추출한 날짜/시간 정보를 리턴
        return expiration;
    }

    // 인코딩된 비밀번호를 디코딩한다.
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        // 파라미터로 받은 인코딩된 비밀번호를 디코드해서 byte 타입의 배열에 넣는다
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        // HMACSHA 알고리즘을 기반으로 디코드된 비밀번호로 서명키를 생성한다
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }
}
