package com.haya.repository;

import com.haya.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);

    boolean existsByTokenAndLoggedOutFalse(String token);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Token t where t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Token t where t.expiresAt is not null and t.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
