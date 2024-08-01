package com.ssafy.signal.member.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class duplicateJson {
    private boolean isDuplicated;

    public duplicateJson(boolean idDuplicated) {
        this.isDuplicated = idDuplicated;
    }

}
