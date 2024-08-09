package com.ssafy.signal.file.service;

import com.ssafy.signal.board.service.BoardService;
import com.ssafy.signal.file.domain.FileDto;
import com.ssafy.signal.file.domain.FileEntity;
import com.ssafy.signal.file.repository.FileRepository;
import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.service.MemberService;
import jakarta.transaction.Transactional;
import com.ssafy.signal.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    public String uploadBoardFile(MultipartFile[] multipartFile, Long boardId) throws IOException {
        if (multipartFile == null || multipartFile.length == 0) {
            log.warn("Please select at least one file to upload.");
            return "Please select at least one file to upload.";
        }

        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile file : multipartFile) {
            if (!file.isEmpty()) {
                try {
                    log.info("{}", file);

                    // S3에 파일 업로드 후 URL 가져오기
                    String url = s3Uploader.upload(file, DIR_NAME);

                    uploadedFileNames.add(url);

                    // 파일 정보를 DB에 저장
                    FileEntity file1 = new FileEntity();
                    file1.setBoard(boardService.getBoardById(boardId));
                    file1.setUser(null);
                    file1.setFileName(file.getOriginalFilename());
                    file1.setFileUrl(url);

                    fileRepository.save(file1);

                } catch (IOException e) {
                    log.warn("Failed to upload one or more files.");
                    return "Failed to upload one or more files.";
                }
            }
        }
        return uploadedFileNames.toString();
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
        return fileRepository.save(file).asFileDto();

    }
    // 파일 삭제하기
    @Transactional
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

    @Transactional
    public String updateBoardFile(MultipartFile[] multipartFile, Long boardId) throws IOException {
        List<String> boards = getFilesByBoardId(boardId);
        List<String> uploadedFileNames = new ArrayList<>();
        log.info("boards: {}", boards);

        for (String board : boards) {
            if (!multipartFile[0].isEmpty()) {
                s3Uploader.delete(board);

                fileRepository.deleteByFileUrl(board);
                log.info("DB에서 삭제되었습니다.");
            }

        }

        for (MultipartFile file : multipartFile) {
            if (!file.isEmpty()) {
                String url = s3Uploader.upload(file, DIR_NAME);

                uploadedFileNames.add(url);

                // 파일 정보를 DB에 저장
                FileEntity file1 = new FileEntity();
                file1.setBoard(boardService.getBoardById(boardId));
                file1.setUser(null);
                file1.setFileName(file.getOriginalFilename());
                file1.setFileUrl(url);

                fileRepository.save(file1);
            }
        }
        return uploadedFileNames.toString();
    }


    @Transactional
    public FileDto updateProfileFile(MultipartFile multipartFile, Long userId) throws IOException {
        // 새로운 파일 URL을 S3에 업로드하여 가져오기
        String newUrl = s3Uploader.upload(multipartFile, DIR_NAME);

        // 기존 파일을 사용자 ID로 찾아서 삭제
        Optional<FileEntity> existingFileOptional = fileRepository.findByUser_UserId(userId);
        if (existingFileOptional.isPresent()) {
            FileEntity existingFile = existingFileOptional.get();
            try {
                // S3에서 기존 파일 삭제
                s3Uploader.delete(existingFile.getFileUrl());

                // DB에서 기존 파일 삭제
                fileRepository.deleteByFileUrl(existingFile.getFileUrl());

            } catch (Exception e) {
                throw new RuntimeException("Failed to delete existing file", e);
            }
        }

        // 새로운 파일 정보를 DB에 저장
        Member user = memberService.getMemberById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        FileEntity newFile = new FileEntity();
        newFile.setBoard(null);
        newFile.setUser(user);
        newFile.setFileName(multipartFile.getOriginalFilename());
        newFile.setFileUrl(newUrl);

        FileDto savedFileDto = fileRepository.save(newFile).asFileDto();

        // 로그 추가
        System.out.println("Saved new file with URL: " + newFile.getFileUrl());

        return savedFileDto;
    }



    public String getProfile(Long userId) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        FileEntity file = fileRepository.findAllByUser(user);

        if (file == null) {
            return "";
        }

        return file.getFileUrl();
    }

}
