package com.haya.controller;

import com.haya.modles.Note;
import com.haya.service.NoteServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Controller
public class NotesController {

    private final NoteServices services;

    public NotesController(NoteServices services) {
        this.services = services;
    }


    //get all notes
    @GetMapping("/note")
    List<Note> getAllNotes(){
        return services.findAllNotes();
    }

    //create a note
    @PostMapping("/note")
    Note insertNote(@RequestBody Note note){
        return services.insertNote(note);
    }

    //edit a note
    @PutMapping("/note/{id}")
    Note UpdateNote(@PathVariable int id, @RequestBody Note newNote){
        return services.updateNote(id,newNote);
    }

    //delete a note
    @DeleteMapping("/note/{id}")
    void deleteNote(@PathVariable int id){
        services.deleteNote(id);
    }


}
