package io.desofme.fileuploaddownload.controller;

import io.desofme.fileuploaddownload.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public String upload(@RequestPart("file")MultipartFile multipartFile){
        return fileService.upload(multipartFile);
    }

    @PostMapping("/remote/upload")
    public String uploadToRemoteServer(@RequestPart("file" )MultipartFile multipartFile){
        return fileService.uploadToRemoteServer(multipartFile);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> get(@PathVariable("fileName") String fileName){
        try {
            File file = new File("src/main/resources/files/" + fileName);
            String contentType = Files.probeContentType(file.toPath());
            byte[] bytes = FileUtils.readFileToByteArray(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new ByteArrayResource(bytes));
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
