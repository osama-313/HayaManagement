package com.haya.controller;

import com.haya.modles.Commitment;
import com.haya.service.CommitmentServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@Controller
public class CommitmentController {

    private final CommitmentServices services;

    public CommitmentController(CommitmentServices services) {
        this.services = services;
    }


    //get all notes
    @GetMapping("/commitment")
    List<Commitment> getAllNotes(){
        return services.findAllNotes();
    }

    //create a note
    @PostMapping("/commitment")
    Commitment insertNote(@RequestBody Commitment note){
        return services.insertNote(note);
    }

    //edit a note
    @PutMapping("/commitment/{id}")
    Commitment UpdateNote(@PathVariable int id, @RequestBody Commitment newNote){
        return services.updateNote(id,newNote);
    }

    //delete a note
    @DeleteMapping("/commitment/{id}")
    void deleteNote(@PathVariable int id){
        services.deleteNote(id);
    }


}
