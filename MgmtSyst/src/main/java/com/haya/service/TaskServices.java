package com.haya.service;

import com.haya.modles.Task;
import com.haya.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServices {

    private TaskRepository repo;

    public TaskServices(TaskRepository repo) {
        this.repo = repo;
    }

    // list all Task
    public List<Task> findAllTasks(){
        return repo.findAll();
    }

    //create a task
    public Task insertTask(Task user){
        return repo.save(user);
    }

    // update/edit task
    public Task UpdateTask(int id, Task newTask){
        Task UpdatedTask = new Task();

        UpdatedTask = repo.findById(id).orElse(null);


        UpdatedTask.setTask(newTask.getTask());
        UpdatedTask.setTaskDescribtion(newTask.getTaskDescribtion());
        UpdatedTask.setTaskStatus(newTask.isTaskStatus());
        UpdatedTask.setDueDate(newTask.getDueDate());

        UpdatedTask.setUser(newTask.getUser());

        return repo.save(UpdatedTask);

    }

    //delete a task
    public void deleteTask (int id){
        repo.deleteById(id);
    }

    // find all tasks to a user with his id
    public List<Task> findAUserTasks(int id){

        List<Task> list = new ArrayList<>();

        for (Task s : repo.findAll()) {
            if (s.getUser().getId() == id){
                list.add(s);
            }
        }

        return list;
    }

}
