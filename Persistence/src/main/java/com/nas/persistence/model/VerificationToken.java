package com.nas.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;

@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String operation;
    private boolean used;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;

    private Instant expiryDate;

    public VerificationToken(String token, String operation, User user) {
        this.token = token;
        this.operation = operation;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
        this.used = false;
    }

    public static int getExpiration() {
        return EXPIRATION;
    }

    private Instant calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.toInstant();
    }
}