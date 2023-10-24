package com.example.wniosekapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.wniosekapp.model.Application;
import com.example.wniosekapp.model.ApplicationChange;
import com.example.wniosekapp.repository.ApplicationChangeRepository;
import com.example.wniosekapp.service.ApplicationChangeService;

@Service
public class ApplicationChangeServiceImpl implements ApplicationChangeService {

	private final ApplicationChangeRepository applicationChangeRepository;

	@Autowired
	public ApplicationChangeServiceImpl(ApplicationChangeRepository applicationChangeRepository) {
		this.applicationChangeRepository = applicationChangeRepository;
	}

	public void logApplicationChange(Application application) {
		logApplicationChange(application, null);
	}

	public void logApplicationChange(Application application, String reason) {
		ApplicationChange applicationChange = new ApplicationChange();
		applicationChange.setApplication(application);
		applicationChange.setTitle(application.getTitle());
		applicationChange.setDescription(application.getDescription());
		applicationChange.setState(application.getState());
		applicationChange.setReason(reason);

		applicationChangeRepository.save(applicationChange);
	}
}
