package com.haya.modles;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(generator = "task_id", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "task_id", allocationSize = 1)
    @Column(name = "task_id" , updatable = false)
    private int id;

    @Column(name = "task", nullable = false)
    private String task;

    @Column(name = "tasksDescribtion", nullable = false)
    private String taskDescribtion;

    @Column(name = "task_status", nullable = false)
    private boolean taskStatus;

    @JoinColumn(name = "users_task", nullable = false)
    @ManyToOne
    private  User user;


    public Task() {

    }

    public Task(int id, String task, String taskDescribtion, boolean taskStatus, User user) {

        this.id = id;
        this.task = task;
        this.taskDescribtion = taskDescribtion;
        this.taskStatus = taskStatus;
        this.user = user;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskDescribtion() {
        return taskDescribtion;
    }

    public void setTaskDescribtion(String taskDescribtion) {
        this.taskDescribtion = taskDescribtion;
    }

    public boolean isTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
