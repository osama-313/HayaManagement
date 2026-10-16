package com.haya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.Locale;

@Entity
@Table(name = "roles")
public class Role {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_id", allocationSize = 1)
    @Column(name = "rold_id", updatable = false) // legacy column name, kept for schema compatibility
    private Long id;

    @Column(name = "user_role", nullable = false)
    private String name;

    protected Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    /** Normalised role name without any {@code ROLE_} prefix, e.g. {@code ADMIN}. */
    public String getNormalizedName() {
        String upper = name.trim().toUpperCase(Locale.ROOT);
        return upper.startsWith("ROLE_") ? upper.substring("ROLE_".length()) : upper;
    }

    /** Spring Security authority for this role, e.g. {@code ROLE_ADMIN}. */
    public String getAuthority() {
        return "ROLE_" + getNormalizedName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
