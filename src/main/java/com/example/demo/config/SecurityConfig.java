package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，方便测试 POST/PUT/DELETE 请求（Postman 不需要处理 Token）
                .csrf(csrf -> csrf.disable())

                // 关键：允许所有请求，无需登录
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 禁用登录页和 Basic 认证（避免浏览器弹窗）
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }


}