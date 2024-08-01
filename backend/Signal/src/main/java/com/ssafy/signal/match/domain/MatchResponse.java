package com.ssafy.signal.match.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MatchResponse {
    long from_id;
    long to_id;

    String name;
    String type;
    String comment;
}
