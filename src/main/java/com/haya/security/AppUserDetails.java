package com.haya.security;

import com.haya.entity.Role;
import com.haya.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Authenticated principal; carries the user id so services can apply ownership rules without another lookup. */
public class AppUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean admin;
    private final List<GrantedAuthority> authorities;

    public AppUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUserName();
        this.password = user.getPassword();
        this.admin = Role.ADMIN.equals(user.getRole().getNormalizedName());
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }

    public Long getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
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
}
