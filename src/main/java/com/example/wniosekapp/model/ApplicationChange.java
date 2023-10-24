package com.example.wniosekapp.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Application_Changes")
@Getter
@Setter
public class ApplicationChange {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "application_id")
	private Application application;

	private String title;

	private String description;

	@ManyToOne
	@JoinColumn(name = "state_id")
	private State state;

	private String reason;
	private LocalDateTime changeDate;

	public ApplicationChange() {
		this.changeDate = LocalDateTime.now();
	}
}
