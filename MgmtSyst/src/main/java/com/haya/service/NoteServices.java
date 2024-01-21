package com.haya.service;


import com.haya.modles.Note;
import com.haya.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServices {

    private NoteRepository repo;

    public NoteServices(NoteRepository repo) {
        this.repo = repo;
    }


    // get all notes
    public List<Note> findAllNotes(){
        return repo.findAll();
    }

    // create/insert notes
    public Note insertNote(Note note){
        return repo.save(note);
    }

    //edit/update note
    public Note updateNote(int id, Note newNote){

        Note updatedNote = new Note();

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
