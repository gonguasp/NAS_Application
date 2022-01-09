package com.nas.mail.event;

import com.nas.persistence.model.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;


public class PreAuthenticationEvent extends ApplicationEvent {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Locale locale;
    private User user;
    private String operation;

    public PreAuthenticationEvent(User user, String operation, Locale locale) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setUser(User user) {
        this.user = user;
    }
}