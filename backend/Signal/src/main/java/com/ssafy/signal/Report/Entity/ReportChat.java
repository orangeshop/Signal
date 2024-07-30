package com.ssafy.signal.Report.Entity;

import com.ssafy.signal.chat.domain.ChatRoomEntity;
import com.ssafy.signal.member.domain.Member;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id")
    private ChatRoomEntity chatId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "target_id", nullable = false)
    private Member targetMemberId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mem_id", nullable = false)
    private Member memberId;

    @Column(name = "report_datetime")
    private LocalDateTime repDatetime;

    @Column(name = "comment", nullable = true)
    private String comment;
}
