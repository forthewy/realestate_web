package com.jane.realestate.config;

import com.jane.realestate.entity.User;
import com.jane.realestate.enums.Role;
import com.jane.realestate.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    // 초기값 생성 (회원가입은 무조건 유저)
//    @Bean
//    CommandLineRunner init(UserRepository userRepository,
//                           PasswordEncoder passwordEncoder) {
//        return args -> {
//
//            if (userRepository.findByUsername("admin").isEmpty()) {
//
//                User admin = User.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin1234"))
//                        .name("관리자")
//                        .role(Role.ADMIN)
//                        .build();
//
//                userRepository.save(admin);
//            }
//        };
//    }
}