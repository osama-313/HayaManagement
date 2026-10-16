package com.haya.service;

import com.haya.entity.Role;
import com.haya.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RoleService {

    private final RoleRepository roles;

    public RoleService(RoleRepository roles) {
        this.roles = roles;
    }

    /**
     * Resolves a role by its name, ignoring case and any {@code ROLE_} prefix, creating it on first use.
     * Matching is done in memory because the roles table only ever holds a handful of rows.
     */
    @Transactional
    public Role require(String name) {
        String normalized = name.trim().toUpperCase(Locale.ROOT);
        return roles.findAll().stream()
                .filter(role -> role.getNormalizedName().equals(normalized))
                .findFirst()
                .orElseGet(() -> roles.save(new Role(normalized)));
    }
}
