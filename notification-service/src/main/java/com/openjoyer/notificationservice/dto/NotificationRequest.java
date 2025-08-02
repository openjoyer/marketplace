package com.openjoyer.notificationservice.dto;

import com.openjoyer.notificationservice.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private NotificationType type;
    private String title;
    private String content;
    private boolean isHtml;
    private String receiverEmail;
}
