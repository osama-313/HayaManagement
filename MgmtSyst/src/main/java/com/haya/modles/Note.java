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

    @Column(name = "note" )
    private String note;

    @JoinColumn(name = "users_note" , nullable = false)
    @ManyToOne
    private User user;

    public Note() {
    }

    public Note(int id, String note, User user) {
        this.id = id;
        this.note = note;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
