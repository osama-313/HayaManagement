package com.haya.repository;

import com.haya.modles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserName(String userName);

    @Transactional
    @Modifying
    @Query("SELECT u.firstName, u.lastName, u.email, u.proficiency FROM User u ")
    List<User> getDashbordInfoForUser();

}
