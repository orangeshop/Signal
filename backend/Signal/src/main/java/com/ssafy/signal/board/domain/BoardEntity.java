package com.ssafy.signal.board.domain;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.findMemberDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
@Table(name="board")
public class BoardEntity extends TimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(length = 10)
    private String writer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member user;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Long reference;

    @Column
    private Long liked;

    @Column
    private Long type;

    @ManyToMany()
    @Builder.Default
    @JoinTable(name="tag_board",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    List<TagEntity> tags = new ArrayList<>();


    @Builder
    public BoardEntity(Long id, String writer, Member user, String title, String content, Long reference, Long liked, Long type, List<TagEntity> tags) {
        this.id = id;
        this.writer = writer;
        this.user = user;
        this.title = title;
        this.content = content;
        this.reference = reference != null ? reference : 0;
        this.liked = liked != null ? liked : 0;
        this.type = type != null ? type : 0;
        this.tags = tags != null ? tags : new ArrayList<>();
    }
    // 엔티티의 상태를 변경하는 메서드 추가
    public void update(String title, String content, Long reference, Long liked, Long type) {
        this.title = title;
        this.content = content;
        this.reference = reference;
        this.liked = liked;
        this.type = type;
    }

    public BoardDto asBoardDto()
    {
        return BoardDto.builder()
                .createdDate(getCreatedDate())
                .modifiedDate(getModifiedDate())
                .content(content)
                .id(id)
                .userId(user.getUserId())
                .writer(writer)
                .title(title)
                .liked(liked)
                .reference(reference)
                .tags(tags.stream().map(TagEntity::asTagDto).toList())
                .type(type)
                .build();
    }

    public BoardDto asBoardDto(List<CommentDto> comments, List<String> fileUrls, findMemberDto profile )
    {
        return BoardDto.builder()
                .createdDate(getCreatedDate())
                .modifiedDate(getModifiedDate())
                .content(content)
                .id(id)
                .member(profile)
                .writer(writer)
                .title(title)
                .userId(user.getUserId())
                .liked(liked)
                .comments(comments)
                .reference(reference)
                .tags(tags.stream().map(TagEntity::asTagDto).toList())
                .type(type)
                .fileUrls(fileUrls)
                .build();
    }
    public void incrementReference(){
        this.reference++;
    }

    public void incrementLiked() {
        this.liked++;
    }
}
