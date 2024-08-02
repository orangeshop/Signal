package com.ssafy.signal.file.service;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.file.domain.FileDto;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileService {

    private final String DIR_NAME = "signal";
//    @Autowired
//    private FileRepository fileRepository;

    @Autowired
    @Lazy
    private MemberService memberService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private S3Uploader s3Uploader;

    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;

    @Autowired
    public FileService(MemberRepository memberRepository, FileRepository fileRepository) {
        this.memberRepository = memberRepository;
        this.fileRepository = fileRepository;
    }

    public String uploadBoardFile(MultipartFile multipartFile, Long boardId) throws IOException {
        // S3에 파일 업로드 후 URL 가져오기
        String url = s3Uploader.upload(multipartFile, DIR_NAME);

        // 파일 정보를 DB에 저장
        FileEntity file = new FileEntity();
        file.setBoard(boardService.getBoardById(boardId));
        file.setUser(null);
        file.setFileName(multipartFile.getOriginalFilename());
        file.setFileUrl(url);

        fileRepository.save(file);

        // 업로드된 파일의 URL 반환
        return url;
    }

    public FileDto uploadProfileFile(MultipartFile multipartFile,  Long userId) throws IOException {
        // S3에 파일 업로드 후 URL 가져오기
        String url = s3Uploader.upload(multipartFile, DIR_NAME);

        // 파일 정보를 DB에 저장
        FileEntity file = new FileEntity();
        file.setBoard(null);
        file.setUser(memberService.getMemberById(userId));
        file.setFileName(multipartFile.getOriginalFilename());
        file.setFileUrl(url);

        fileRepository.save(file);

        // 업로드된 파일의 URL 반환

        return new FileDto(null, null, null, null, null, url, null, null);
    }
    // 파일 삭제하기
    public void deleteFile(Long id) throws  IOException{

            // 파일 이름으로 파일 정보 찾기
                Optional<FileEntity> fileEntityOptional = fileRepository.findById(id);
            if (fileEntityOptional.isPresent()) {
                FileEntity fileEntity = fileEntityOptional.get();

                // S3에서 파일 삭제
                s3Uploader.delete(fileEntity.getFileUrl());

                // DB에서 파일 정보 삭제
                fileRepository.delete(fileEntity);
            }
    }

    public List<FileDto> getAllFiles() {
        List<FileEntity> fileEntities = fileRepository.findAll();
        return fileEntities.stream()
                .map(FileEntity::asFileDto)
                .collect(Collectors.toList());
    }


    public List<String> getFilesByBoardId(Long boardId) {
        return fileRepository.findByBoardId(boardId).stream()
                .map(FileEntity::getFileUrl)
                .collect(Collectors.toList());
    }

    public String getProfile(Long userId) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        FileEntity file = fileRepository.findAllByUser(user);

        if (file == null) {
            return null;
        }

        return file.getFileUrl();
    }

}
