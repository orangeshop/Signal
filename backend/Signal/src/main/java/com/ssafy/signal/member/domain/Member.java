package com.ssafy.signal.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "login_id", nullable = false)
    private String login_id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "type")
    private int type;

    @Column(name = "name")
    private String name;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    // refreshToken 재설정
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 비밀번호 암호화
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

}
