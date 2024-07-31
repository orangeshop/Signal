package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.TagEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    public List<BoardDto>getBoardByTagRecent(String tag,int page,int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);

        List<BoardDto> boards = new ArrayList<>(tagEntity.getBoard().stream().map(BoardEntity::asBoardDto).toList());
        boards.sort(Comparator.comparing(BoardDto::getCreatedDate).reversed());
        return boards
                .subList(page * limit, Math.min(page * limit + limit,boards.size()));
    }

    public List<BoardDto> getBoardByTagHot(String tag, int page, int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);

        // 태그와 관련된 모든 게시물 가져오기
        List<BoardDto> boards = new ArrayList<>(tagEntity.getBoard().stream()
                .map(BoardEntity::asBoardDto)
                .filter(board -> board.getLiked() >= 10) // 좋아요 수가 10 이상인 게시물만 필터링
                .toList());

        // 좋아요 수를 기준으로 내림차순 정렬
        boards.sort(Comparator.comparing(BoardDto::getLiked).reversed());

        // 페이지에 맞게 서브리스트 반환
        return boards.subList(page * limit, Math.min(page * limit + limit, boards.size()));
    }
}
