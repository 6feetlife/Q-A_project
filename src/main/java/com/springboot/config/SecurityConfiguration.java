package com.springboot.config;

import com.springboot.auth.filter.JwtAuthenticationFilter;
import com.springboot.auth.filter.JwtVerificationFilter;
import com.springboot.auth.handler.MemberAccessDeniedHandler;
import com.springboot.auth.handler.MemberAuthenticationEntryPoint;
import com.springboot.auth.handler.MemberAuthenticationFailureHandler;
import com.springboot.auth.handler.MemberAuthenticationSuccessHandler;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.CustomAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    public SecurityConfiguration(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Bean
    // SecurityFilterChain = HTTP 요청을 필터링하는 보안 체인
    // HttpSecurity = 보안 설정을 정의하는 객체
    // throws Exception = 보안 설정을 적용하는 과정에서 예외 가능성이 있으므로 선언
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // h2 화면 자체가 내부적으로 <frame> 태그를 사용하고 있어 맞춰 사용함
                .headers().frameOptions().sameOrigin()
                .and()
                // csrf 공격방어 비활성화 ( 로컬환경에서 진행함으로 불필요 & 비활성화 안하면 403에러 발생 )
                .csrf().disable()
                // CORS 설정 추가 withDefaults() 라면 corsConfigurationSource 이름으로 등록된 bean 사용
                .cors(Customizer.withDefaults())
                // 세션을 생성하지 않도록 설정 / 시큐리티 자체에서 세션을 자동으로 생성하기때문에 JWT 를 사용한다면 설정을 잡아줘야한다
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 폼로그인 비활성화
                .formLogin().disable()
                // HTTP Basic = 로그인창 팝업을 띄우는 방식
                .httpBasic().disable()
                // 예외 핸들링
                .exceptionHandling()
                // 인증 예외 포인트 설정
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                // 접근 거부 예외 설정
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                // 모든 요청에 대해 인증 없이 접근 가능
                // 여러개의 요청에 대한 권한 정의가 가능하다
                .authorizeHttpRequests(authorize -> authorize
                        // 접근 권한과 상관없이 post 요청이라면 허용한다
                        .antMatchers(HttpMethod.POST, "/*/members").permitAll()
                        .antMatchers(HttpMethod.POST, "/*/coffees").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/*/orders").hasRole("USER")
                        // 정보 수정 요청은 USER 권한만 가능하다
                        .antMatchers(HttpMethod.PATCH,"/*/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/*/coffees/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/*/orders/**").hasAnyRole("USER", "ADMIN")
                        // 회원 전체 조회 요청은 ADMIN 만 가능하다
                        .antMatchers(HttpMethod.GET, "/*/members").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/coffees").permitAll()
                        .antMatchers(HttpMethod.GET, "/*/orders").hasAnyRole("USER", "ADMIN")
                        // 회원 단일 조회 요청은 USER ADMIN 만 가능
                        .antMatchers(HttpMethod.GET,"/*/members/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/*/coffees/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/*/orders/**").hasAnyRole("USER", "ADMIN")
                        // 회원 삭제 요청은 USER 만 가능
                        .antMatchers(HttpMethod.DELETE, "/*/members/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/coffees/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/orders/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll()
                );
        // http 객체를 SecurityFilterChain 으로 변환하여 반환
        return http.build();
    }

    @Bean
    // 비밀번호 암호화 역할을 하는 bean 을 생성하는 메서드
    public PasswordEncoder passwordEncoder() {
        // 자동으로 적절한 암호화 알고리즘을 선택하는 PasswordEncoder 생성
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
        // 구체적인 CORS 정책을 설정
    CorsConfigurationSource corsConfigurationSource() {
        // CORS 설정 정보 담는 객체 생성
        CorsConfiguration configuration = new CorsConfiguration();
        // 스크립트 기반의 HTTP 통신 = 클라이언트측 언어가 브라우저에서 실행되면서 발생하는 HTTP 요청을 의미
        // ex) AJAX 요청, Fetch API 등
        // 모든 출처(Origin)에 대해 스크립트 기반의 HTTP 통신을 허용하도록 설정
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // 파라미터로 지정한 HTTP Method 에 대한 HTTP 통신을 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        // CORS 정책을 URL 패턴별로 설정하는 클래스, 특정 URL(endPoint) 에 대해 CORS 정책을 다르게 적용할때 사용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 위에서 설정한 CORS 정책(configuration)을 특정 URL 경로에 대해서만 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // JwtAuthenticationFilter 등록
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        // httpSecurity 객체를 받아 보안 설정 적용
        public void configure(HttpSecurity builder) throws Exception {
            // AuthenticationManager 객체 생성
            AuthenticationManager authenticationManager =
                    // HttpSecurity 객체 내부의 AuthenticationManger 타입의 공유 객체를 불러온다
                    builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter =
                    new JwtAuthenticationFilter(authenticationManager,jwtTokenizer);
            // 디폴트 request URL 인 "/login" 을 "/v11/auth/login" 으로 변경
            jwtAuthenticationFilter.setFilterProcessesUrl("/v11/auth/login");
            // 인증 성공 핸들러 추가
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            // 인증 실패 핸들러 추가
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            // JwtAuthenticationFilter 를 Spring Security Filter Chain 에 추가
            builder.addFilter(jwtAuthenticationFilter)
                    // 로그인 인증에 성공한후 발급받은 JWT 가 클라이언트의 request 헤더에 포함되어 있을 경우에만 동작
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}
