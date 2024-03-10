package com.haya.controller;

import com.haya.modles.Resources;
import com.haya.service.ResourcesServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@Controller
public class ResuorceController {

    private final ResourcesServices services;


    public ResuorceController(ResourcesServices services) {
        this.services = services;
    }

    @GetMapping("/resource")
    List<Resources> findAllResources(){
        return services.findAllResources();
    }

    @PostMapping("/resource")
    Resources insertResourse(@RequestBody Resources resource){
        return services.insertResource(resource);
    }

    @PostMapping("/resource/{id}")
    Resources updateResouce(@PathVariable int id, Resources resources){
        return services.updateResource(id,resources);
    }

    @DeleteMapping("/resource/{id}")
    void DeleteResource(@PathVariable int id){
        services.deleteNote(id);
    }

}
