package com.openjoyer.authservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHandler {
    private int httpStatus;
    private String message;
    private LocalDateTime timestamp;
}
