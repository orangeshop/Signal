package com.ssafy.signal.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    private Long userId;

    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    @NotBlank(message = "로그인은 필수 입력값입니다.")
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "name", length = 50)
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

}
