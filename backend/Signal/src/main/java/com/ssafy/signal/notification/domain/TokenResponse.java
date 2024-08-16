package com.ssafy.signal.notification.domain;

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
