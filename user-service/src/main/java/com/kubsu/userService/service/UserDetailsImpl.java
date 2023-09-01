package com.kubsu.userService.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubsu.userService.model.Group;
import com.kubsu.userService.model.Role;
import com.kubsu.userService.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {

    private Long id;

    private Long kubsuUserId;

    private String username;

    private String fullName;

    private String email;

    @JsonIgnore
    private String password;

    private Group group;

    private OffsetDateTime startEducationDate;

    private OffsetDateTime endEducationDate;

    private OffsetDateTime creationDate;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           Long kubsuUserId,
                           String username,
                           String fullName,
                           String email,
                           String password,
                           Group group,
                           OffsetDateTime startEducationDate,
                           OffsetDateTime endEducationDate,
                           OffsetDateTime creationDate,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.kubsuUserId = kubsuUserId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.group = group;
        this.startEducationDate = startEducationDate;
        this.endEducationDate = endEducationDate;
        this.creationDate = creationDate;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getId(),
                user.getKubsuUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPassword(),
                user.getGroup(),
                user.getStartEducationDate(),
                user.getEndEducationDate(),
                user.getCreationDate(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }
}
