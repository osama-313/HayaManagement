package com.haya.service;

import com.haya.dto.TaskDtos;
import com.haya.entity.Task;
import com.haya.entity.User;
import com.haya.exception.NotFoundException;
import com.haya.repository.TaskRepository;
import com.haya.repository.UserRepository;
import com.haya.security.AppUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Everyone on the team can see every task. Administrators manage any task; regular members only manage
 * tasks assigned to themselves.
 */
@Service
public class TaskService {

    private final TaskRepository tasks;
    private final UserRepository users;

    public TaskService(TaskRepository tasks, UserRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<TaskDtos.Response> findAll(Long assigneeId) {
        List<Task> found = assigneeId == null
                ? tasks.findAllByOrderByIdDesc()
                : tasks.findByAssigneeIdOrderByIdDesc(assigneeId);
        return found.stream().map(TaskDtos.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskDtos.Response findById(Long id) {
        return TaskDtos.Response.from(require(id));
    }

    @Transactional
    public TaskDtos.Response create(TaskDtos.Request request, AppUserDetails actor) {
        requireMayAssignTo(request.assigneeId(), actor);
        Task task = new Task(request.title().trim(), blankToNull(request.description()),
                blankToNull(request.dueDate()), request.completed(), requireUser(request.assigneeId()));
        return TaskDtos.Response.from(tasks.save(task));
    }

    @Transactional
    public TaskDtos.Response update(Long id, TaskDtos.Request request, AppUserDetails actor) {
        Task task = require(id);
        requireMayManage(task, actor);
        requireMayAssignTo(request.assigneeId(), actor);

        task.setTitle(request.title().trim());
        task.setDescription(blankToNull(request.description()));
        task.setDueDate(blankToNull(request.dueDate()));
        task.setCompleted(request.completed());
        if (!task.getAssignee().getId().equals(request.assigneeId())) {
            task.setAssignee(requireUser(request.assigneeId()));
        }
        return TaskDtos.Response.from(task);
    }

    @Transactional
    public void delete(Long id, AppUserDetails actor) {
        Task task = require(id);
        requireMayManage(task, actor);
        tasks.delete(task);
    }

    private Task require(Long id) {
        return tasks.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    }

    private User requireUser(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    private static void requireMayManage(Task task, AppUserDetails actor) {
        if (!actor.isAdmin() && !task.getAssignee().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Not your task");
        }
    }

    private static void requireMayAssignTo(Long assigneeId, AppUserDetails actor) {
        if (!actor.isAdmin() && !actor.getId().equals(assigneeId)) {
            throw new AccessDeniedException("Only administrators can assign tasks to other members");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
