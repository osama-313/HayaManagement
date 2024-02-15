package com.haya.modles;


import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(generator = "note_id", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "note_id", allocationSize = 1)
    @Column(name = "note_id" , updatable = false)
    private int id;

    @Column(name = "From_to_When" )
    private String fromToWhen;


    @Column(name = "hours" )
    private int hours;

    @Column(name = "note" )
    private String note;

    @JoinColumn(name = "users_note" , nullable = false)
    @ManyToOne
    private User user;

    public Note() {
    }

    public Note(int id, String fromToWhen, int hours, String note, User user) {
        this.id = id;
        this.fromToWhen = fromToWhen;
        this.hours = hours;
        this.note = note;
        this.user = user;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromToWhen() {
        return fromToWhen;
    }

    public void setFromToWhen(String fromToWhen) {
        this.fromToWhen = fromToWhen;
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
