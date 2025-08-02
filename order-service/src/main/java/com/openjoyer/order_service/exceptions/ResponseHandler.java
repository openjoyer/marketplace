package com.openjoyer.order_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseHandler {
    private int httpStatus;
    private String message;
    private LocalDateTime timestamp;
}
