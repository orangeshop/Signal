package com.ssafy.signal.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    private String login_id;

    @Column(name = "password", nullable = false, length = 50, unique = true)
    private String password;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "name", length = 50)
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
