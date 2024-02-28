package com.haya.modles;


import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Commitment {

    @Id
    @GeneratedValue(generator = "note_id", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "note_id", allocationSize = 1)
    @Column(name = "note_id" , updatable = false)
    private int id;

    @Column(name = "From_When" )
    private String fromWhen;

    @Column(name = "To_When" )
    private String toWhen;

    @Column(name = "hours" )
    private int hours;

    @Column(name = "note" )
    private String note;

    @JoinColumn(name = "users_note" , nullable = false)
    @ManyToOne
    private User user;

    public Commitment(int id, String fromWhen, String toWhen, int hours, String note, User user) {
        this.id = id;
        this.fromWhen = fromWhen;
        this.toWhen = toWhen;
        this.hours = hours;
        this.note = note;
        this.user = user;
    }

    public Commitment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setUser(User user) {
        this.user = user;
    }
}
