package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.file.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuplicateService {
    private final BoardService boardService;
    private final FileService fileService;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public BoardDto getPost(Long id) {
        Optional<BoardEntity> boardEntityWrapper = boardRepository.findById(id);
        BoardEntity boardEntity = boardEntityWrapper.orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 댓글 조회
        List<CommentDto> comments = commentRepository.findByBoardId(id).stream()
                .map(CommentEntity::asCommentDto)
                .collect(Collectors.toList());

        // 파일 URL 조회 (FileService를 통해)
        List<String> fileUrls = fileService.getFilesByBoardId(id); // boardId로 파일 URL 가져오기

        return boardEntity.asBoardDto(comments, fileUrls); // 파일 URL을 포함하여 DTO 생성
    }
}
