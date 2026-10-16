package com.haya.controller;

import com.haya.dto.TaskDtos;
import com.haya.security.AppUserDetails;
import com.haya.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService tasks;

    public TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @GetMapping
    @Operation(summary = "List tasks, newest first, optionally only those assigned to one member")
    List<TaskDtos.Response> list(@RequestParam(required = false) Long assigneeId) {
        return tasks.findAll(assigneeId);
    }

    @GetMapping("/{id}")
    TaskDtos.Response get(@PathVariable Long id) {
        return tasks.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TaskDtos.Response create(@Valid @RequestBody TaskDtos.Request request,
                             @AuthenticationPrincipal AppUserDetails actor) {
        return tasks.create(request, actor);
    }

    @PutMapping("/{id}")
    TaskDtos.Response update(@PathVariable Long id, @Valid @RequestBody TaskDtos.Request request,
                             @AuthenticationPrincipal AppUserDetails actor) {
        return tasks.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails actor) {
        tasks.delete(id, actor);
    }
}
