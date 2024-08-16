package com.ssafy.signal.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tag")
@Builder // 이 줄을 추가하세요
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name="tag_name")
    private String tagName;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    List<BoardEntity> board = new ArrayList<>();

    public TagDto asTagDto()
    {
        return TagDto.builder()
                .id(id)
                .tagName(tagName)
                .build();
    }

}
