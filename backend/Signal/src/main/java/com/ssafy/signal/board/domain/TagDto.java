package com.ssafy.signal.board.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDto {
    private Long id;
    private String tagName;

    public TagEntity toEntity() {
        return TagEntity.builder()
                .id(id)
                .tagName(tagName)
                .build();
    }
}
