package com.example.wniosekapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.wniosekapp.model.Application;
import com.example.wniosekapp.model.State;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
	Page<Application> findByStateAndTitleContaining(State state, String name, Pageable pageable);

	Page<Application> findByTitleContaining(String name, Pageable pageable);

	Page<Application> findByState(State state, Pageable pageable);

}
