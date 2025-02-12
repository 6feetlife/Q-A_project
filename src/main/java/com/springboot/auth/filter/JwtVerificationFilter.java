package com.springboot.auth.filter;

import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer,
                                 CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Override
    //
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 파라미터로 받은 요청에서 JWT 를 검증하고 Map 구조의 claims 에 담는다
            Map<String, Object> claims = verifyJws(request);
            // 검증된 정보를 담은 claims 를 SecurityContext 에 셋팅
            setAuthenticationToContext(claims);
            // 서명이 유효하지 않을때 예외 캐치
        } catch (SignatureException se) {
            // 이때 Attribute 는 HttpServletRequest 객체에 데이터를 저장하는 키-값 이다.
            // request 객체에 exception 이라는 이름을 가진 속성에 se 값을 저장하는것이다.
            request.setAttribute("exception", se);
            // 만료기한이 유효하지 않을때 예외 캐치
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
            // 이외에 예외가 터졌을때 예외 캐치
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
        // 다음 필터로 request 와 response 를 담아서 전달
        filterChain.doFilter(request, response);
    }

    @Override
    // JWT 가 인증 헤더에 포함되지 않았다면 자격증명이 필요하지 않은 리소스에 대한 요청이라고 판단후
    // 다음 필터로 처리를 넘겨줌
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // request 의 Authorization header 의 값을 얻는다
        String authorization = request.getHeader("Authorization");
        // Authorization header 의 값이 null 이거나 Authorization header 의 값이
        // "Bearer"로 시작하지 않는다면 해당 Filter 의 동작을 수행하지 않도록 정의
        return authorization == null || !authorization.startsWith("Bearer");
    }

    // JWT 를 검증하는데 사용되는 메서드
    private Map<String, Object> verifyJws(HttpServletRequest request) {
        // HTTP 요청에서 Authorization 헤더 값을 가져온다.
        // 이때 Bearer(공백포함) 을 제거한 순수한 JWT 값만 추출한다
        String jws = request.getHeader("Authorization").replace("Bearer ","");
        // 시크릿키를 인코딩해서 가져온다
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        // JWT 를 검증하고 payload(Claims) 데이터를 Map 형태로 추출
        // getClaims() 는 JWT 의 서명을 검증한 후, Payload 부분을 파싱하여 반환
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();
        return claims;
    }

    // 시큐리티 context 에 있는 인증 정보를 변경
    private void setAuthenticationToContext(Map<String, Object> claims) {
        // payload 에서 username 가져오는데 String 으로 형변환 해줘야함
        String username = (String)claims.get("username");
        // payload 에서 권한 목록 가져와서 권한 생성후 리스트화
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        // username 과 password 가 들어간 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        // 시큐리티 context 에 있는 인증 정보를 현재 생성한 인증 정보로 교체
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
