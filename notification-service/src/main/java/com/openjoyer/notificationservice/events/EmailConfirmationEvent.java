package com.openjoyer.notificationservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailConfirmationEvent {
    private String email;
    private String confirmationToken;
}

