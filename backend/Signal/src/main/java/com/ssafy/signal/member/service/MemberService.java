package com.ssafy.signal.member.service;

import com.ssafy.signal.board.domain.BoardDto;
import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.domain.CommentDto;
import com.ssafy.signal.board.domain.CommentEntity;
import com.ssafy.signal.board.repository.CommentRepository;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.file.service.FileService;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.dto.LoginDto;
import com.ssafy.signal.member.dto.MyProfileDto;
import com.ssafy.signal.member.dto.MypageUpdateDto;
import com.ssafy.signal.member.dto.findMemberDto;
import com.ssafy.signal.member.jwt.AccessTokenBlackList;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.principle.UserPrinciple;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.member.repository.TokenBlacklistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final FileRepository fileRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final FileService fileService;
    private final CommentRepository commentRepository;
    private final AccessTokenBlackList accessTokenBlackList;


    public Boolean chekcLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            log.info("이미 등록된 아이디 = {}", loginId);
            return false;
        } else {
            return true;
        }
    }

    public Member saveMember(Member member) {
        checkPasswordStrength(member.getPassword());

        if (memberRepository.existsByLoginId(member.getLoginId())) {
            log.info("이미 등록된 아이디 = {}", member.getLoginId());
            throw new IllegalStateException("이미 등록된 아이디입니다.");
        }
        Member member1 = Member.builder()
                .userId(member.getUserId())
                .loginId(member.getLoginId())
                .password(passwordEncoder.encode(member.getPassword()))
                .type(member.getType())
                .name(member.getName())
                .comment(member.getComment())
                .build();

        return memberRepository.save(member1);
    }


    public TokenInfo loginMember(String loginId, String password) {
        try {
            Member member = findMemberByLoginId(loginId);

            LoginDto member1 = LoginDto.builder()
                    .userId(member.getUserId())
                    .loginId(member.getLoginId())
                    .password(member.getPassword())
                    .type(member.getType())
                    .name(member.getName())
                    .comment(member.getComment() == null? "" : member.getComment())
                    .score(member.getScore())
                    .build();

            checkPassword(password, member);

            return tokenProvider.createToken(member1);
        } catch (IllegalArgumentException | BadCredentialsException exception) {
//            throw new IllegalArgumentException("계정이 존재하지 않거나 비밀번호가 잘못되었습니다.");

            return TokenInfo.builder()
                    .status(false)
                    .member(null)
                    .tokenId(null)
                    .accessToken(null)
                    .accessTokenExpireTime(null)
                    .refreshToken(null)
                    .refreshTokenExpireTime(null)
                    .build();
        }
    }

    public TokenInfo autologinMember(String loginId, String password) {
        try {
            Member member = findMemberByLoginId(loginId);

            LoginDto member1 = LoginDto.builder()
                    .userId(member.getUserId())
                    .loginId(member.getLoginId())
                    .password(member.getPassword())
                    .type(member.getType())
                    .name(member.getName())
                    .comment(member.getComment() == null? "" : member.getComment())
                    .score(member.getScore())
                    .build();

            autocheckPassword(password, member);

            return tokenProvider.createToken(member1);
        } catch (IllegalArgumentException | BadCredentialsException exception) {
//            throw new IllegalArgumentException("계정이 존재하지 않거나 비밀번호가 잘못되었습니다.");

            return TokenInfo.builder()
                    .status(false)
                    .member(null)
                    .tokenId(null)
                    .accessToken(null)
                    .accessTokenExpireTime(null)
                    .refreshToken(null)
                    .refreshTokenExpireTime(null)
                    .build();
        }
    }

    public void logout(String accessToken, String loginId) {
        accessTokenBlackList.setBlackList(accessToken, loginId);
    }

    private void checkPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.info("일치하지 않는 비밀번호");
            log.info("{}, {}",password,member.getLoginId());
            throw new BadCredentialsException("기존 비밀번호 확인에 실패했습니다.");
        }
//        if (!password.equals(member.getPassword())) {
//            log.info("일치하지 않는 비밀번호");
//            throw new BadCredentialsException("기존 비밀번호 확인에 실패했습니다.");
//        }
    }

    private void autocheckPassword(String password, Member member) {
//        if (!passwordEncoder.matches(password, member.getPassword())) {
//            log.info("일치하지 않는 비밀번호");
//            throw new BadCredentialsException("기존 비밀번호 확인에 실패했습니다.");
//        }
        if (!password.equals(member.getPassword())) {
            log.info("일치하지 않는 비밀번호");
            throw new BadCredentialsException("기존 비밀번호 확인에 실패했습니다.");
        }
    }

    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() -> {
            log.info("계정이 존재하지 않음");
            return new IllegalArgumentException("계정이 존재하지 않습니다.");
        });
    }

    @Transactional
    public MypageUpdateDto updateMember(Long userId, Member updatedMember) {
        Member existingMember = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
            // checkPasswordStrength(updatedMember.getPassword());
            existingMember.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
        }
        if (updatedMember.getType() != null && !updatedMember.getType().isEmpty()) {
            existingMember.setType(updatedMember.getType());
        }
        if (updatedMember.getName() != null && !updatedMember.getName().isEmpty()) {
            existingMember.setName(updatedMember.getName());
        }
        if (updatedMember.getComment() != null && !updatedMember.getComment().isEmpty()) {
            existingMember.setComment(updatedMember.getComment());
        }
        Member member1 = memberRepository.save(existingMember);

        return MypageUpdateDto.builder()
                .userId(member1.getUserId())
                .loginId(member1.getLoginId())
                .password(member1.getPassword())
                .name(member1.getName())
                .type(member1.getType())
                .comment(member1.getComment())
                .score(member1.getScore())
                .build();
    }

    public MyProfileDto getUserInfo(String loginId) {
        Member myProfile = findMemberByLoginId(loginId);
        String url = fileService.getProfile(myProfile.getUserId());

        return MyProfileDto.builder()
                .userId(myProfile.getUserId())
                .loginId(myProfile.getLoginId())
                .type(myProfile.getType())
                .name(myProfile.getName())
                .profileImage(url)
                .comment(myProfile.getComment() == null? "" : myProfile.getComment())
                .score(myProfile.getScore())
                .build();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));
    }

    public findMemberDto findMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));
        String url = fileService.getProfile(id);

        return findMemberDto.builder()
                .userId(id)
                .profileImage(url)
                .name(member.getName())
                .type(member.getType())
                .score(member.getScore())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));
//        return new org.springframework.security.core.userdetails.User(member.getLoginId(), member.getPassword(), new ArrayList<>());
        return new UserPrinciple(member.getLoginId(), member.getLoginId(), new ArrayList<>());
    }

    private void checkPasswordStrength(String password) {
        if (PASSWORD_PATTERN.matcher(password).matches()) {
            return;
        }

        log.info("비밀번호 정책 미달");
        throw new IllegalArgumentException("비밀번호는 최소 8자리에 영어, 숫자, 특수문자를 포함해야 합니다.");
    }

    public void deleteMemberByLoginId(String loginId) {
        memberRepository.deleteByLoginId(loginId);
    }

    //내가 쓴 글 조회
    public List<BoardDto> getMemberWithPosts(Long userId) throws Exception {
        Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new Exception("Member Not found"));

        return member.getBoards().stream()
                .map(this::addFileUrlAndComments)
                .collect(Collectors.toList());
    }

    // 내가 쓴 댓글의 게시글 조회
    public List<BoardDto> getMemberCommentedPosts(Long userId) throws Exception {
        Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new Exception("Member Not found"));

        return member.getComments().stream()
                .map(CommentEntity::getBoardEntity) // 댓글에서 게시글을 가져옴
                .distinct() // 중복된 게시글 제거
                .map(this::addFileUrlAndComments) // 게시글에 대한 URL 추가
                .collect(Collectors.toList());
    }

    private BoardDto addFileUrlAndComments(BoardEntity boardEntity) {
        // 파일 URL 가져오기 (게시판용)
        List<String> fileUrls = fileService.getFilesByBoardId(boardEntity.getId());

        // 댓글 정보 가져오기
        List<CommentDto> comments = commentRepository.findByBoardId(boardEntity.getId()).stream()
                .map(comment ->{
                    String url = fileService.getProfile(comment.getUserId().getUserId());
                    return comment.asCommentDto(url);
                })
                .collect(Collectors.toList());

        // 게시글 작성자의 정보 가져오기
        Member member = boardEntity.getUser();
        String profileUrl = fileService.getProfile(member.getUserId());

        findMemberDto profile = findMemberDto.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .profileImage(profileUrl)
                .build();

        // BoardDto 생성 및 반환
        return boardEntity.asBoardDto(comments, fileUrls, profile);
    }

    public void upscore(Long userId) throws Exception {
        Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new Exception("Member Not found"));

        int score = member.getScore();

        member.setScore(score+1);

        memberRepository.save(member);
    }

}
