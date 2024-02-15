package com.haya.modles;

import jakarta.persistence.*;
@Entity
@Table(name = "resources")
public class Resources {

    @Id
    @GeneratedValue(generator = "role_id", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "role_id", allocationSize = 1)
    @Column(name = "Resource_id" , updatable = false)
    private int id;

    @Column(name = "url")
    private String url;

    @Column(name = "highlights")
    private String highlights;


    public Resources() {
    }

    public Resources(int id, String url, String highlights) {
        this.id = id;
        this.url = url;
        this.highlights = highlights;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
