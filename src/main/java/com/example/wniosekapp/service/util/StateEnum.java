package com.example.wniosekapp.service.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateEnum {
	CREATED(1L),
	VERIFIED(2L),
	ACCEPTED(3L),
	PUBLISHED(4L),
	REJECTED(5L),
	DELETED(6L);

	private final Long id;

}
