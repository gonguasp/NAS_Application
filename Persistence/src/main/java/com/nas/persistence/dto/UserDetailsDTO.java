package com.nas.persistence.dto;

import com.nas.persistence.model.UserView;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UserDetailsDTO implements UserDetails {
    private static final long serialVersionUID = 1L;
    private long id;
    private String userName;
    private String password;
    private String name;
    private boolean active;
    private List<GrantedAuthority> authorities;

    public UserDetailsDTO(UserView userView) {
        this.id = userView.getId();
        this.userName = userView.getEmail();
        this.password = userView.getPassword();
        this.name = userView.getName();
        this.active = userView.getActiveSince() != null && userView.isEnable();
        this.authorities = Arrays.stream(userView.getRoles().split(", "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public UserDetailsDTO setUsername(String userName) {
        this.userName = userName;
        return this;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public String getName() {
        return name;
    }

}