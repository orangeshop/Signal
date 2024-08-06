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

    @Transactional
    public BoardDto getPost(Long id) {
        // 게시글 엔티티 조회
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 게시글 작성자의 userId 가져오기
        Member member = boardEntity.getUser();

        // 댓글 조회
        List<CommentDto> comments = commentRepository.findByBoardId(id).stream()
                .map(comment ->{
                    String url = fileService.getProfile(comment.getUserId().getUserId());
                    return comment.asCommentDto(url);
                })
                .collect(Collectors.toList());

        // 파일 URL 조회 (게시판용)
        List<String> fileUrls = fileService.getFilesByBoardId(id); // boardId로 파일 URL 가져오기

        String profileUrl = fileService.getProfile(member.getUserId());

        findMemberDto profile = findMemberDto.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .profileImage(profileUrl)
                .build();
        // BoardDto 생성
        return boardEntity.asBoardDto(comments, fileUrls, profile);
    }

    // 생성된 순서대로 게시글 정렬 (오늘의 시그널)
    @Transactional
    public List<BoardDto> getBoardList(Integer pageNum, int limit) {
        return boardRepository
                .findAll(PageRequest.of(pageNum, limit, Sort.by(Sort.Direction.DESC, "createdDate")))
                .getContent()
                .stream()
                .map(boardEntity -> {
                    // 게시글 작성자의 정보 가져오기
                    Member member = boardEntity.getUser();
                    String profileUrl = fileService.getProfile(member.getUserId());

                    findMemberDto profile = findMemberDto.builder()
                            .userId(member.getUserId())
                            .name(member.getName())
                            .profileImage(profileUrl)
                            .build();

                    // 댓글 정보 가져오기
                    List<CommentDto> comments = commentRepository.findByBoardId(boardEntity.getId()).stream()
                            .map(comment ->{
                                String url = fileService.getProfile(comment.getUserId().getUserId());
                                return comment.asCommentDto(url);
                            })
                            .collect(Collectors.toList());

                    // 파일 URL 가져오기 (게시판용)
                    List<String> fileUrls = fileService.getFilesByBoardId(boardEntity.getId());

                    // BoardDto 생성 및 반환
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
                    // 게시글 작성자의 정보 가져오기
                    Member member = boardEntity.getUser();
                    String profileUrl = fileService.getProfile(member.getUserId());

                    findMemberDto profile = findMemberDto.builder()
                            .userId(member.getUserId())
                            .name(member.getName())
                            .profileImage(profileUrl)
                            .build();

                    // 댓글 정보 가져오기
                    List<CommentDto> comments = commentRepository.findByBoardId(boardEntity.getId()).stream()
                            .map(comment ->{
                                String url = fileService.getProfile(comment.getUserId().getUserId());
                                return comment.asCommentDto(url);
                            })
                            .collect(Collectors.toList());

                    // 파일 URL 가져오기 (게시판용)
                    List<String> fileUrls = fileService.getFilesByBoardId(boardEntity.getId());

                    // BoardDto 생성 및 반환
                    return boardEntity.asBoardDto(comments, fileUrls, profile);
                })
                .collect(Collectors.toList());
    }

}
