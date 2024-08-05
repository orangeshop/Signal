package com.ssafy.signal.board.domain;

import com.ssafy.signal.file.domain.FileDto;
import com.ssafy.signal.member.dto.findMemberDto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.ssafy.signal.member.domain.Member;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    private String writer;
    private Long userId; // 새로운 필드 추가
    private findMemberDto member;
    private String title;
    private String content;
    private Long reference;
    private Long liked;
    private Long type;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<CommentDto> comments;
    private List<TagDto> tags;
    private List<String> fileUrls;



    public BoardEntity toEntity(){
        return  BoardEntity.builder()
                .id(id)
                .writer(writer)
                .title(title)
                .content(content)
                .reference(reference)
                .liked(liked)
                .type(type)
                .tags(tags.stream().map(TagDto::toEntity).toList())
                .user(Member.builder().userId(userId).build()) // Member 객체 참조
                .build();
    }

    public BoardEntity toEntity(List<CommentDto> comments){
        return  BoardEntity.builder()
                .id(id)
                .writer(writer)
                .title(title)
                .content(content)
                .reference(reference)
                .liked(liked)
                .type(type)
                .tags(tags.stream().map(TagDto::toEntity).toList())
                .user(Member.builder().userId(userId).build()) // Member 객체 참조
                .build();
    }
}
