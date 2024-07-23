package com.ssafy.signal.Report.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_chat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportChat {

    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "target_member_id", nullable = false)
    private Long targetMemberId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "report_datetime")
    private LocalDateTime repDatetime;

    @Column(name = "comment", nullable = true)
    private String comment;
}
