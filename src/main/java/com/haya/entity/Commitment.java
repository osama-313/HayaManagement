package com.haya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/** A block of time a team member commits to work, with an optional note. */
@Entity
@Table(name = "notes")
public class Commitment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commitment_seq")
    @SequenceGenerator(name = "commitment_seq", sequenceName = "note_id", allocationSize = 1)
    @Column(name = "note_id", updatable = false)
    private Long id;

    /** Start time as {@code HH:mm}. */
    @Column(name = "From_When")
    private String fromWhen;

    /** End time as {@code HH:mm}. */
    @Column(name = "To_When")
    private String toWhen;

    private int hours;

    @Column(length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_note", nullable = false)
    private User user;

    protected Commitment() {
    }

    public Commitment(String fromWhen, String toWhen, int hours, String note, User user) {
        this.fromWhen = fromWhen;
        this.toWhen = toWhen;
        this.hours = hours;
        this.note = note;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getFromWhen() {
        return fromWhen;
    }

    public void setFromWhen(String fromWhen) {
        this.fromWhen = fromWhen;
    }

    public String getToWhen() {
        return toWhen;
    }

    public void setToWhen(String toWhen) {
        this.toWhen = toWhen;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }
}
