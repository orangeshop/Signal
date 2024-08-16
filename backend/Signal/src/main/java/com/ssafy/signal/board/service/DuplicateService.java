package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.file.domain.FileDto;
import com.ssafy.signal.file.service.FileService;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.findMemberDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuplicateService {
    private final BoardService boardService;
    private FileService fileService;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private findMemberDto buildMemberProfile(Member member) {
        String profileUrl = fileService.getProfile(member.getUserId());
        return findMemberDto.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .profileImage(profileUrl)
                .type(member.getType())  // 필요시 추가
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

    @Transactional
    public BoardDto getPost(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        Member member = boardEntity.getUser();

        findMemberDto profile = buildMemberProfile(member);
        List<CommentDto> comments = getComments(id);
        List<String> fileUrls = getFileUrls(id);

        return boardEntity.asBoardDto(comments, fileUrls, profile);
    }

    @Transactional
    public List<BoardDto> getBoardList(Integer pageNum, int limit) {
        return boardRepository
                .findAll(PageRequest.of(pageNum, limit, Sort.by(Sort.Direction.DESC, "createdDate")))
                .getContent()
                .stream()
                .map(boardEntity -> {
                    Member member = boardEntity.getUser();
                    findMemberDto profile = buildMemberProfile(member);
                    List<CommentDto> comments = getComments(boardEntity.getId());
                    List<String> fileUrls = getFileUrls(boardEntity.getId());

                    return boardEntity.asBoardDto(comments, fileUrls, profile);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BoardDto> getBoardListLiked(Integer pageNum, int limit) {
        return boardRepository
                .findAll(PageRequest.of(pageNum, limit, Sort.by(Sort.Direction.DESC, "liked")))
                .getContent()
                .stream()
                .map(boardEntity -> {
                    Member member = boardEntity.getUser();
                    findMemberDto profile = buildMemberProfile(member);
                    List<CommentDto> comments = getComments(boardEntity.getId());
                    List<String> fileUrls = getFileUrls(boardEntity.getId());

                    return boardEntity.asBoardDto(comments, fileUrls, profile);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BoardDto> searchPosts(String keyword) {
        List<BoardEntity> boardEntities = boardRepository.searchByTitleOrContent(keyword);
        List<BoardDto> boardDtoList = new ArrayList<>();

        if (boardEntities.isEmpty()) return boardDtoList;

        for (BoardEntity boardEntity : boardEntities) {
            Member member = boardEntity.getUser();
            findMemberDto profile = buildMemberProfile(member);
            List<CommentDto> comments = getComments(boardEntity.getId());
            List<String> fileUrls = getFileUrls(boardEntity.getId());

            boardDtoList.add(boardEntity.asBoardDto(comments, fileUrls, profile));
        }
        return boardDtoList;
    }

}
