package com.example.wniosekapp.service;

import com.example.wniosekapp.model.Application;

public interface ApplicationChangeService {

	void logApplicationChange(Application application);

	void logApplicationChange(Application application, String reason);
}
