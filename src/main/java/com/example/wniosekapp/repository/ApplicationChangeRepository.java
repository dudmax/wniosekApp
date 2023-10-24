package com.example.wniosekapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.wniosekapp.model.ApplicationChange;

@Repository
public interface ApplicationChangeRepository extends JpaRepository<ApplicationChange, Long> {
}
