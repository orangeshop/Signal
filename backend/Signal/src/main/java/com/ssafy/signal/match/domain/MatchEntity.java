package com.ssafy.signal.match.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "match_history")
public class MatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long match_id;

    @ManyToOne
    @JoinColumn(name="propose_id")
    Member proposeId;

    @ManyToOne
    @JoinColumn(name="accept_id")
    Member acceptId;


    public MatchDto asMatchDto()
    {
        return MatchDto.builder()
                .match_id(match_id)
                .proposeId(proposeId.getUserId())
                .acceptId(acceptId.getUserId())
                .build();
    }
}
