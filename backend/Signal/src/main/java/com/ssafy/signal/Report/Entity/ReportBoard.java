package com.ssafy.signal.Report.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "board_id", length = 20, nullable = false)
    private Long boardId;

    @Column(name = "mem_id", length = 20, nullable = false)
    private Long userId;

    @Column(name = "comment_id", length = 20, nullable = false)
    private Long commentId;

    @Column(name = "target_type", length = 1, nullable = false)
    private Integer targetType;

    @Column(name = "report_datetime", nullable = false)
    private LocalDateTime repDatetime;

    @Column(name = "comment")
    private String comment;
}
