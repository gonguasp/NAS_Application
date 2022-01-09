package com.nas.persistence.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String password;
    private String name;
    private boolean enable;
    private Instant lastLogin;
    private Instant lastModification;
    private Instant activeSince;
    private boolean darkTheme;

    @OneToMany(mappedBy = "user")
    private List<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<VerificationToken> verificationToken;

    @OneToMany(mappedBy = "user")
    private List<File> file;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.enable = false;
        this.lastLogin = null;
        this.activeSince = null;
        this.lastModification = null;
        this.darkTheme = false;
    }

    public User(UserView userView) {
        this.id = userView.getId();
        this.email = userView.getEmail();
        this.password = userView.getPassword();
        this.name = userView.getName();
        this.enable = userView.isEnable();
        this.lastLogin = userView.getLastLogin();
        this.activeSince = userView.getActiveSince();
        this.lastModification = userView.getLastModification();
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastModification = Instant.now();
    }

    public void setVerificationToken(List<VerificationToken> verificationToken) {
        this.verificationToken = verificationToken;
        this.lastModification = Instant.now();
    }

    public void setEnabled(boolean enable) {
        this.enable = enable;
        this.activeSince = Instant.now();
        this.lastModification = this.activeSince;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", name=" + name + "]";
    }
}