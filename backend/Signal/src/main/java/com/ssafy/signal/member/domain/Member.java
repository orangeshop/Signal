package com.ssafy.signal.member.domain;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "comment", nullable = true)
    private String comment;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardEntity> boards = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>() ;
}
