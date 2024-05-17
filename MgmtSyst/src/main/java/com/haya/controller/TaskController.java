package com.haya.controller;


import com.haya.modles.Task;
import com.haya.service.TaskServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://195.35.32.197:4200")
@RestController
@RequestMapping("/api")
@Controller
public class TaskController {

    private final TaskServices services;

    public TaskController(TaskServices services) {
        this.services = services;
    }


    @GetMapping("/task")
    List<Task> GetAllTasks(){
        return services.findAllTasks();
    }

    @PostMapping("/task")
    Task createTask(@RequestBody Task task){
        return services.insertTask(task);
    }

    @PutMapping("/task/{id}")
    Task UpdateTask(@PathVariable int id,@RequestBody Task newTask){
        return services.UpdateTask(id,newTask);
    }

    @DeleteMapping("/task/{id}")
    void deleteTask(@PathVariable int id)
    {
        services.deleteTask(id);
    }

    @GetMapping("/task/{id}")
    List<Task> findUserTasks(@PathVariable int id){
        return services.findAUserTasks(id);
    }
}
