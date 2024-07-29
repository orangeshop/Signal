package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.TagEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {
    private static final int BLOCK_PAGE_NUM_COUNT = 5; // 블럭에 존재하는 페이지 번호 수
    private static final int PAGE_POST_COUNT = 4; // 한 페이지에 존재하는 게시글 수

    private final TagRepository tagRepository;
    private final BoardRepository boardRepository;
    //private final BoardTagRepository boardTagRepository;

    public List<BoardDto>getBoardByTagRecent(String tag,int page,int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);
        List<BoardEntity> boards = tagEntity.getBoards().stream().toList();
        boards.sort(Comparator.comparing(BoardEntity::getCreatedDate).reversed());

        return boards.stream().map(BoardEntity::asBoardDto)
                .toList()
                .subList(page * limit, page * limit + limit);
    }

    public List<BoardDto>getBoardByTagHot(String tag,int page,int limit) {
        TagEntity tagEntity = tagRepository.findByTagName(tag);
        List<BoardEntity> boards = tagEntity.getBoards().stream().filter(board->board.getLiked()>=10).toList();
        boards.sort(Comparator.comparing(BoardEntity::getLiked).reversed());

        return boards.stream().map(BoardEntity::asBoardDto)
                .toList()
                .subList(page * limit, page * limit + limit);
    }
}
