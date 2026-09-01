package com.jane.realestate.config;

import com.jane.realestate.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // CSRF 보호 비활성화 (JWT 기반 REST API 사용)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 API
                        // 로그인 / 회원가입 / 토큰 재발급
                        // 실거래 조회
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/transactions/**"
                        ).permitAll()
                        // 관리자 API는 ADMIN 권한만 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그외는 인증 필요
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter보다 먼저 실행
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
