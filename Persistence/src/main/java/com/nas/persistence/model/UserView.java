package com.nas.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Immutable
@Table(name = "users_view")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserView {
    @Id
    private long id;
    private String email;
    private String password;
    private String name;
    private boolean enable;
    private Instant lastLogin;
    private Instant lastModification;
    private Instant activeSince;
    private String roles;
    private Long size;
    private boolean darkTheme;

    public UserView(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.enable = user.isEnable();
        this.lastLogin = user.getLastLogin();
        this.lastModification = user.getLastModification();
        this.activeSince = user.getActiveSince();
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getRole()));
        this.roles = String.join(", ", roles);
        this.darkTheme = user.isDarkTheme();
    }
}