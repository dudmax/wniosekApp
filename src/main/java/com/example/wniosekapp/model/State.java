package com.example.wniosekapp.model;

import com.example.wniosekapp.service.util.StateEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "States")
@Getter
@Setter
@NoArgsConstructor
public class State {
	@Id
	@GeneratedValue()
	private Long id;
	private String name;

	public State(StateEnum stateEnum) {
		this.id = stateEnum.getId();
		this.name = stateEnum.name();
	}
}


