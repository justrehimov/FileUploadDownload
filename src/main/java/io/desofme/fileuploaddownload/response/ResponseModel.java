package io.desofme.fileuploaddownload.response;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseModel implements Serializable {
    private FileUploadResponse result;
    private int code;
    private Boolean error;
}
