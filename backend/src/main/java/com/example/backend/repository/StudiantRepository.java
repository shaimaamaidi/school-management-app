package com.example.backend.repository;

import com.example.backend.entity.Studiant;
import com.example.backend.enumeration.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface StudiantRepository extends JpaRepository<Studiant,Long> {
    Page<Studiant> findByUsernameContainingIgnoreCase(String username,Pageable pageable);
    Page<Studiant> findByLevel(Level level,Pageable pageable);
    Optional<Studiant> findByUsername(String username);
}
