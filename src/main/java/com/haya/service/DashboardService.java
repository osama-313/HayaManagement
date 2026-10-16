package com.haya.service;

import com.haya.dto.DashboardDtos;
import com.haya.dto.UserDtos;
import com.haya.repository.CommitmentRepository;
import com.haya.repository.ResourceRepository;
import com.haya.repository.TaskRepository;
import com.haya.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final UserRepository users;
    private final TaskRepository tasks;
    private final CommitmentRepository commitments;
    private final ResourceRepository resources;

    public DashboardService(UserRepository users, TaskRepository tasks, CommitmentRepository commitments,
                            ResourceRepository resources) {
        this.users = users;
        this.tasks = tasks;
        this.commitments = commitments;
        this.resources = resources;
    }

    /** Aggregates everything the dashboard needs in a handful of grouped queries instead of loading all rows. */
    @Transactional(readOnly = true)
    public DashboardDtos.Summary summary() {
        Map<Long, long[]> countsByUser = new HashMap<>();
        long open = 0;
        long completed = 0;
        for (TaskRepository.WorkloadRow row : tasks.countByAssigneeAndStatus()) {
            long[] counts = countsByUser.computeIfAbsent(row.getUserId(), id -> new long[2]);
            if (row.isCompleted()) {
                counts[1] += row.getTotal();
                completed += row.getTotal();
            } else {
                counts[0] += row.getTotal();
                open += row.getTotal();
            }
        }

        var workload = users.findAll().stream()
                .map(user -> {
                    long[] counts = countsByUser.getOrDefault(user.getId(), new long[2]);
                    return new DashboardDtos.MemberWorkload(UserDtos.Summary.from(user), user.getEmail(),
                            user.getProficiency(), user.getRole().getNormalizedName(), counts[0], counts[1]);
                })
                .toList();

        return new DashboardDtos.Summary(workload.size(), open, completed,
                tasks.countOverdue(LocalDate.now().toString()), commitments.totalHours(), resources.count(), workload);
    }
}
