package com.haya.repository;

import com.haya.modles.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserName(String userName);

}
