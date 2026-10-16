package com.haya.dto;

import java.util.List;

public final class DashboardDtos {

    private DashboardDtos() {
    }

    public record Summary(
            long members,
            long openTasks,
            long completedTasks,
            long overdueTasks,
            long committedHours,
            long resources,
            List<MemberWorkload> workload) {
    }

    public record MemberWorkload(
            UserDtos.Summary member,
            String email,
            String proficiency,
            String role,
            long openTasks,
            long completedTasks) {
    }
}
