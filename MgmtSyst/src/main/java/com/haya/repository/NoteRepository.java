package com.haya.repository;

import com.haya.modles.Commitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Commitment,Integer> {

}
