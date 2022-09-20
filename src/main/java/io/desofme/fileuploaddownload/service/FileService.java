package io.desofme.fileuploaddownload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.desofme.fileuploaddownload.response.ResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String upload(MultipartFile multipartFile) {
        try {
            if (multipartFile.isEmpty()) {
                throw new IllegalArgumentException("File can't be empty");
            }

            String fileName = multipartFile.getOriginalFilename();
            File file = new File("src/main/resources/files/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            return "http://localhost:8080/files/" + fileName;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public String uploadToRemoteServer(MultipartFile multipartFile) {
        try {
            String url = "https://vusalrehimov.000webhostapp.com/upload.php";
            File file = multipartFileToFile(multipartFile);
            MultiValueMap multiValueMap = getMultiValueMap(file);
            Object result = webClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.parseMediaType(multipartFile.getContentType()))
                    .body(BodyInserters.fromMultipartData(multiValueMap))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            file.delete();

            String response = objectMapper.writeValueAsString(result);

            ResponseModel fileUploadResponseResponseModel =
                    objectMapper.readValue(response, ResponseModel.class);

            if (fileUploadResponseResponseModel.getError()) {
                throw new IllegalArgumentException("File hasn't been uploaded");
            } else {
                return fileUploadResponseResponseModel.getResult().getUrl();
            }
        }catch (Exception ex){
            log.error("Error", ex);
            ex.printStackTrace();
            return null;
        }
    }

    public MultiValueMap<String, HttpEntity<?>> getMultiValueMap(File file){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        return builder.build();
    }

    public File multipartFileToFile(MultipartFile multipartFile) throws Exception{
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }
}
