package com.openjoyer.notificationservice.service;

import com.openjoyer.notificationservice.dto.NotificationRequest;
import com.openjoyer.notificationservice.model.Notification;
import com.openjoyer.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    private final EmailSender emailSender;

    public void sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .receiverEmail(request.getReceiverEmail())
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .isHtml(request.isHtml())
                .sentAt(LocalDateTime.now())
                .build();

        if (notification.isHtml()) {
            emailSender.sendHtml(notification);
        }
        else {
            emailSender.send(notification);
        }

        repo.save(notification);
        //TODO логика получения email юзера по айди / передача по http в заголовках / получение из JWT
    }
}
