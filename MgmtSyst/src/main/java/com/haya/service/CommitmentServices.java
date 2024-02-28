package com.haya.service;


import com.haya.modles.Commitment;
import com.haya.repository.CommitmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommitmentServices {

    private final CommitmentRepository repo;

    public CommitmentServices(CommitmentRepository repo) {
        this.repo = repo;
    }

    // get all notes
    public List<Commitment> findAllNotes(){
        return repo.findAll();
    }

    // create/insert notes
    public Commitment insertNote(Commitment note){
        return repo.save(note);
    }

    //edit/update note
    public Commitment updateNote(int id, Commitment newNote){

        Commitment updatedNote = new Commitment();

        updatedNote = repo.findById(id).orElse(null);

        updatedNote.setNote(newNote.getNote());
        updatedNote.setUser(newNote.getUser());

        return repo.save(updatedNote);
    }

    // Delete Notes
    public void deleteNote(int id){
        repo.deleteById(id);
    }

}
