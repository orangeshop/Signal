package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.*;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.board.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public List<BoardDto> getBoardByTagRecent(String tag, int page, int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);

        if (tagEntity == null) {
            return Collections.emptyList();
        }

        // 태그에 해당하는 게시글을 생성 날짜 기준으로 내림차순 정렬
        List<BoardDto> boards = tagEntity.getBoard().stream()
                .map(BoardEntity::asBoardDto)
                .sorted(Comparator.comparing(BoardDto::getCreatedDate).reversed())
                .collect(Collectors.toList());

        int start = page * limit;
        int end = Math.min(start + limit, boards.size());
        if (start >= end) {
            return Collections.emptyList(); // 페이지가 범위를 벗어난 경우 빈 리스트 반환
        }
        List<BoardDto> pagedBoards = boards.subList(start, end);

        // 각 게시글에 댓글 설정
        for (BoardDto board : pagedBoards) {
            long id = board.getId();
            List<CommentDto> comments = commentRepository
                    .findByBoardId(id)
                    .stream()
                    .map(CommentEntity::asCommentDto)
                    .collect(Collectors.toList());

            board.setComments(comments);
        }

        return pagedBoards;
    }

    public List<BoardDto> getBoardByTagHot(String tag, int page, int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);

        List<BoardDto> boards = new ArrayList<>(tagEntity.getBoard().stream()
                .map(BoardEntity::asBoardDto)
                .filter(board -> board.getLiked() >= 10) // 좋아요 수가 10 이상인 게시물만 필터링
                .collect(Collectors.toList()));
        boards.sort(Comparator.comparing(BoardDto::getLiked).reversed());

        boards = boards.subList(page * limit, Math.min(page * limit + limit, boards.size()));

        for (BoardDto board : boards) {
            long id = board.getId();
            List<CommentDto> comments = commentRepository
                    .findByBoardId(id)
                    .stream()
                    .map(CommentEntity::asCommentDto)
                    .collect(Collectors.toList());

            board.setComments(comments);
        }

        return boards;
    }
}
