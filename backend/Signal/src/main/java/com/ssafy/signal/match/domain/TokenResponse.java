package com.ssafy.signal.match.domain;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {
    private long user_id;
    private String token;

}
