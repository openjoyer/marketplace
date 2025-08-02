package com.openjoyer.notificationservice.controller;

import com.openjoyer.notificationservice.dto.NotificationRequest;
import com.openjoyer.notificationservice.model.Notification;
import com.openjoyer.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationTestController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            notificationService.sendNotification(request);
        } catch (Exception ex) {
            return new ResponseEntity<>("not sent", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("sent", HttpStatus.OK);
    }
}
