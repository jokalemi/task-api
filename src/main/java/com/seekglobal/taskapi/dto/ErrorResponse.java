package com.seekglobal.taskapi.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
}
