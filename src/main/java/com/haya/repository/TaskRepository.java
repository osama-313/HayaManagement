package com.haya.repository;

import com.haya.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /** Task counts per assignee and completion state, used by the dashboard. */
    interface WorkloadRow {
        Long getUserId();

        boolean isCompleted();

        long getTotal();
    }

    @EntityGraph(attributePaths = {"assignee", "assignee.role"})
    List<Task> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"assignee", "assignee.role"})
    List<Task> findByAssigneeIdOrderByIdDesc(Long assigneeId);

    @Query("select t.assignee.id as userId, t.completed as completed, count(t) as total "
            + "from Task t group by t.assignee.id, t.completed")
    List<WorkloadRow> countByAssigneeAndStatus();

    @Query("select count(t) from Task t where t.completed = false "
            + "and t.dueDate is not null and t.dueDate <> '' and t.dueDate < :today")
    long countOverdue(@Param("today") String today);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Task t where t.assignee.id = :userId")
    void deleteByAssigneeId(@Param("userId") Long userId);
}
