package com.openjoyer.marketplace.profile_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(generator = "mongo-like-id")
    @GenericGenerator(name = "mongo-like-id", strategy = "com.openjoyer.marketplace.profile_service.util.IdGenerator")
    @Column(columnDefinition = "TEXT")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

//    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "delivery_address", columnDefinition = "jsonb")
    private Address address;

    @Column(name = "email_verified")
    private boolean isEmailVerified;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "confirmation_token_expiry")
    private LocalDateTime confirmationTokenExpiry;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}