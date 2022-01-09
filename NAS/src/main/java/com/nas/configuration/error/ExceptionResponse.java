package com.nas.configuration.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ExceptionResponse {

    @NonNull
    private Instant timeStamp;
    @NonNull
    private String message;
    @NonNull
    private String details;
    @NonNull
    private HttpStatus httpStatus;

}
