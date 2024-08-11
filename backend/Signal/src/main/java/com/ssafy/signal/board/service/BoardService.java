package com.ssafy.signal.board.service;

import com.ssafy.signal.board.domain.*;
import com.ssafy.signal.board.repository.BoardRepository;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.board.repository.LikeRepository;
import com.ssafy.signal.board.repository.TagRepository;
import com.ssafy.signal.file.service.FileService;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.findMemberDto;
import com.ssafy.signal.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Setter
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private static final int BLOCK_PAGE_NUM_COUNT = 5; // 블럭에 존재하는 페이지 번호 수
    private static final int PAGE_POST_COUNT = 4; // 한 페이지에 존재하는 게시글 수


    @Transactional
    public Long getBoardCount() {
        return boardRepository.count();
    }


    @Transactional
    public BoardDto savePost(BoardDto boardDto) {
        //System.out.println(boardDto.getContent());
        return boardRepository.save(boardDto.toEntity()).asBoardDto();
    }

    @Transactional
    public BoardDto updatePost(Long id, BoardDto boardDto) {
        // 1. 업데이트할 게시물 엔티티 조회
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(id);
        if (!boardEntityOptional.isPresent()) {
            throw new EntityNotFoundException("Board not found with id: " + id);
        }
        BoardEntity boardEntity = boardEntityOptional.get();

        // 2. 엔티티의 업데이트 메서드 호출
        boardEntity.update(boardDto.getTitle() == null? boardEntity.getTitle():boardDto.getTitle()
                , boardDto.getContent() == null? boardEntity.getContent():boardDto.getContent()
                , boardDto.getReference() == null? boardEntity.getReference():boardDto.getReference()
                , boardDto.getLiked() == null? boardEntity.getLiked():boardDto.getLiked()
                , boardDto.getType() == null? boardEntity.getType():boardDto.getType());
        // 필요에 따라 다른 필드들도 업데이트

        // 3. 엔티티 저장 (업데이트된 엔티티를 저장하면 JPA가 자동으로 업데이트 처리)
        boardRepository.save(boardEntity);

        // 업데이트된 엔티티를 DTO로 변환하여 반환
        List<CommentDto> comments = commentRepository.findByBoardId(id).stream()
                .map(CommentEntity::asCommentDto)
                .collect(Collectors.toList());

        return convertEntityToDto(boardEntity, comments);
    }

    @Transactional
    public void deletePost(Long id) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(id);
        if (!boardEntityOptional.isPresent()) {
            throw new EntityNotFoundException("Board not found with id: " + id);
        }
        boardRepository.deleteById(id);
    }

    public Integer[] getPageList(Integer curPageNum) {
        Integer[] pageList = new Integer[100];

        // 총 게시글 갯수
        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        // 총 게시글 기준으로 계산한 마지막 페이지 번호 계산
        Integer totalLastPageNum = (int) (Math.ceil((postsTotalCount / PAGE_POST_COUNT)));

        // 현재 페이지를 기준으로 블럭의 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT
                : totalLastPageNum;

        // 페이지 시작 번호 조정
        curPageNum = (curPageNum <= 3) ? 1 : curPageNum - 2;

        // 페이지 번호 할당
        for (int val = curPageNum, idx = 0; val < blockLastPageNum; val++, idx++) {
            pageList[idx] = val;
        }

        return pageList;
    }

    private BoardDto convertEntityToDto(BoardEntity boardEntity, List<CommentDto> comments) {
        return BoardDto.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .writer(boardEntity.getWriter())
                .userId(boardEntity.getUser().getUserId())
                .reference(boardEntity.getReference())
                .liked(boardEntity.getLiked())
                .type(boardEntity.getType())
                .createdDate(boardEntity.getCreatedDate())
                .modifiedDate(boardEntity.getModifiedDate())
                .tags(boardEntity.getTags().stream().map(TagEntity::asTagDto).toList())
                .comments(comments)
                .build();
    }

    private BoardDto convertEntityToDto(BoardEntity boardEntity) {
        return convertEntityToDto(boardEntity, new ArrayList<>());
    }

    public BoardEntity getBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Board not found with id: " + id));
    }

    @Transactional
    public void incrementReferenceCount(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        boardEntity.incrementReference();
        boardRepository.save(boardEntity);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long boardId, Long userId) {
        // BoardEntity 조회
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // Member 객체 조회
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 좋아요 레코드 조회
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndBoardEntity(user, boardEntity);

        boolean isLiked;
        if (existingLike.isPresent()) {
            LikeEntity likeEntity = existingLike.get();
            if (likeEntity.isLiked()) {
                // 이미 좋아요가 눌려있는 상태 -> 좋아요 취소
                likeEntity.unlike();
                boardEntity.decrementLiked(); // 좋아요 수 감소
                isLiked = false; // 좋아요 상태를 false로 설정
            } else {
                // 좋아요가 눌리지 않은 상태 -> 좋아요 활성화
                likeEntity.like();
                boardEntity.incrementLiked(); // 좋아요 수 증가
                isLiked = true; // 좋아요 상태를 true로 설정
            }
            likeRepository.save(likeEntity);
        } else {
            // 좋아요가 존재하지 않으면 새로 추가
            LikeEntity newLike = new LikeEntity(user, boardEntity);
            likeRepository.save(newLike);
            boardEntity.incrementLiked(); // 좋아요 수 증가
            isLiked = true; // 새로 추가된 경우 좋아요 상태는 true
        }

        // 게시글의 좋아요 수를 갱신
        boardRepository.save(boardEntity);

        // 좋아요 수와 상태를 반환
        Map<String, Object> response = new HashMap<>();
        response.put("likedCount", boardEntity.getLiked());
        response.put("isLiked", isLiked);
        return response;
    }

}
