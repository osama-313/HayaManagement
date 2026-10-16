package com.haya.dto;

import com.haya.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class TaskDtos {

    private TaskDtos() {
    }

    public record Request(
            @NotBlank @Size(max = 120) String title,
            @Size(max = 1000) String description,
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "must be a date formatted as yyyy-MM-dd") String dueDate,
            boolean completed,
            @NotNull Long assigneeId) {
    }

    public record Response(
            Long id,
            String title,
            String description,
            String dueDate,
            boolean completed,
            UserDtos.Summary assignee) {

        public static Response from(Task task) {
            return new Response(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(),
                    task.isCompleted(), UserDtos.Summary.from(task.getAssignee()));
        }
    }
}
