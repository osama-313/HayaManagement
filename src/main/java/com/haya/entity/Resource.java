package com.haya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/** A link the team shares, together with a short description of why it is useful. */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_seq")
    @SequenceGenerator(name = "resource_seq", sequenceName = "Resource_id", allocationSize = 1)
    @Column(name = "Resource_id", updatable = false)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(length = 500)
    private String highlights;

    protected Resource() {
    }

    public Resource(String url, String highlights) {
        this.url = url;
        this.highlights = highlights;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }
}
