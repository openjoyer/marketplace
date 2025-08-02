package com.openjoyer.product_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHandler {
    private int httpStatus;
    private String message;
    private LocalDateTime timestamp;
}
