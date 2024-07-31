package com.ssafy.signal.Match.domain;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class TokenResponse
{
    private long user_id;
    private String token;
}