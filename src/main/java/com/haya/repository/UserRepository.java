package com.haya.repository;

import com.haya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUserNameIgnoreCaseAndIdNot(String userName, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
