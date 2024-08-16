package com.ssafy.signal.report.Entity;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_board")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private BoardEntity boardId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mem_id", nullable = false)
    private Member userId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentId;

    @Column(name = "target_type", length = 1, nullable = false)
    private Integer targetType;

    @Column(name = "report_datetime", nullable = false)
    private LocalDateTime repDatetime;

    @Column(name = "comment")
    private String comment;
}
