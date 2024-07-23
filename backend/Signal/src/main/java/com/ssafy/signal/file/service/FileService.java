package com.ssafy.signal.file.service;

import com.ssafy.signal.board.domain.BoardEntity;
import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private S3Uploader s3Uploader;

    public String uploadBoardFile(MultipartFile multipartFile, String dirName, Long boardId) throws IOException {
        // S3에 파일 업로드 후 URL 가져오기
        String url = s3Uploader.upload(multipartFile, dirName);

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

    public String uploadProfileFile(MultipartFile multipartFile, String dirName, Long userId) throws IOException {
        // S3에 파일 업로드 후 URL 가져오기
        String url = s3Uploader.upload(multipartFile, dirName);

        // 파일 정보를 DB에 저장
        FileEntity file = new FileEntity();
        file.setBoard(null);
        file.setUser(memberService.getMemberById(userId));
        file.setFileName(multipartFile.getOriginalFilename());
        file.setFileUrl(url);

        fileRepository.save(file);

        // 업로드된 파일의 URL 반환
        return url;
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
}
