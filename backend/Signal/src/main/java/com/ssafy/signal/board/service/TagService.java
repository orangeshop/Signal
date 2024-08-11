package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.*;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.board.repository.TagRepository;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.file.service.FileService;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.findMemberDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    private findMemberDto buildMemberProfile(Member member) {
        String profileUrl = fileService.getProfile(member.getUserId());
        return findMemberDto.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .profileImage(profileUrl)
                .build();
    }

    private List<CommentDto> getComments(Long boardId) {
        return commentRepository.findByBoardId(boardId).stream()
                .map(comment -> {
                    String url = fileService.getProfile(comment.getUserId().getUserId());
                    return comment.asCommentDto(url);
                })
                .collect(Collectors.toList());
    }

    private List<String> getFileUrls(Long boardId) {
        return fileService.getFilesByBoardId(boardId);
    }

    private BoardDto buildBoardDto(BoardEntity boardEntity) {
        Member member = boardEntity.getUser();
        findMemberDto profile = buildMemberProfile(member);
        List<CommentDto> comments = getComments(boardEntity.getId());
        List<String> fileUrls = getFileUrls(boardEntity.getId());

        return boardEntity.asBoardDto(comments, fileUrls, profile);
    }

    @Transactional
    public List<BoardDto> getBoardByTagRecent(String tag, int page, int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);

        if (tagEntity == null) {
            return Collections.emptyList();
        }

        List<BoardDto> boards = tagEntity.getBoard().stream()
                .sorted(Comparator.comparing(BoardEntity::getCreatedDate).reversed())
                .map(this::buildBoardDto)
                .collect(Collectors.toList());

        int start = page * limit;
        int end = Math.min(start + limit, boards.size());
        if (start >= end) {
            return Collections.emptyList();
        }

        return boards.subList(start, end);
    }

    @Transactional
    public List<BoardDto> getBoardByTagHot(String tag, int page, int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);

        if (tagEntity == null) {
            return Collections.emptyList();
        }

        List<BoardDto> boards = tagEntity.getBoard().stream()
                .map(this::buildBoardDto)
                .filter(board -> board.getLiked() >= 10)
                .sorted(Comparator.comparing(BoardDto::getLiked).reversed())
                .collect(Collectors.toList());

        int start = page * limit;
        int end = Math.min(start + limit, boards.size());
        if (start >= end) {
            return Collections.emptyList();
        }

        return boards.subList(start, end);
    }
}
