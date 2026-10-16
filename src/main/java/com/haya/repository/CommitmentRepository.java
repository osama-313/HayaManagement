package com.haya.repository;

import com.haya.entity.Commitment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommitmentRepository extends JpaRepository<Commitment, Long> {

    @EntityGraph(attributePaths = {"user", "user.role"})
    List<Commitment> findAllByOrderByIdDesc();

    @Query("select coalesce(sum(c.hours), 0) from Commitment c")
    long totalHours();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Commitment c where c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
