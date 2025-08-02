package com.openjoyer.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    private String id;
//    @Field(name = "receiver_id")
//    private String receiverId;
    private String receiverEmail;
    private String title;
    private String content;
    @Field("is_html")
    private boolean isHtml;
    private NotificationType type;
//    private boolean isRead;
    @Field(name = "sent_at")
    private LocalDateTime sentAt;
}
