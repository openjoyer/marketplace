package com.openjoyer.notificationservice.service;

import com.openjoyer.notificationservice.model.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSender {
    private final JavaMailSender mailSender;


    public void sendHtml(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setText(notification.getContent(), true);
            helper.setTo(notification.getReceiverEmail());
            helper.setSubject(notification.getTitle());

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error while sending email to {}", notification.getReceiverEmail());
            throw new RuntimeException(e);
        }
    }

    public void send(Notification request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getReceiverEmail());
        message.setSubject(request.getTitle());
        message.setText(request.getContent());

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending email to {}, {}", request.getReceiverEmail(), e.getMessage());
            throw e;
        }
    }
}
