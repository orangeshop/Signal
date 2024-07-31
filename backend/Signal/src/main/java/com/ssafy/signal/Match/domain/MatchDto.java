package com.ssafy.signal.Match.domain;

import com.ssafy.signal.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MatchDto {

    private Long match_id;

    long proposeId;
    long acceptId;

    public MatchEntity asMatchEntity() {
        return MatchEntity
                .builder()
                .proposeId(Member.builder().userId(proposeId).build())
                .acceptId(Member.builder().userId(acceptId).build())
                .build();
    }
}
