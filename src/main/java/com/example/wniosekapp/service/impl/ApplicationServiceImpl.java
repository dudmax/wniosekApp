package com.example.wniosekapp.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.wniosekapp.exception.ApplicationNotFoundException;
import com.example.wniosekapp.exception.IncorrectParamException;
import com.example.wniosekapp.exception.IncorrectStateException;
import com.example.wniosekapp.exception.StateNotFoundException;
import com.example.wniosekapp.model.Application;
import com.example.wniosekapp.model.State;
import com.example.wniosekapp.model.dto.ApplicationDTO;
import com.example.wniosekapp.repository.ApplicationRepository;
import com.example.wniosekapp.repository.StateRepository;
import com.example.wniosekapp.service.ApplicationChangeService;
import com.example.wniosekapp.service.ApplicationService;
import com.example.wniosekapp.service.util.StateEnum;

@Service
public class ApplicationServiceImpl implements ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final StateRepository stateRepository;
	private final ApplicationChangeService applicationChangeService;

	public ApplicationServiceImpl(ApplicationRepository applicationRepository, StateRepository stateRepository, ApplicationChangeService applicationChangeService) {
		this.applicationRepository = applicationRepository;
		this.stateRepository = stateRepository;
		this.applicationChangeService = applicationChangeService;
	}

	@Override
	public Page<ApplicationDTO> getApplications(int page, int size, String name, String state) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Application> applications;
		if (name != null && state != null) {
			State stateObject = stateRepository.findByName(state);
			if (stateObject == null) {
				throw new StateNotFoundException();
			}
			applications = applicationRepository.findByStateAndTitleContaining(stateObject, name, pageable);
		} else if (name != null) {
			applications = applicationRepository.findByTitleContaining(name, pageable);
		} else if (state != null) {
			State stateObject = stateRepository.findByName(state);
			if (stateObject == null) {
				throw new StateNotFoundException();
			}
			applications = applicationRepository.findByState(stateObject, pageable);
		} else {
			applications = applicationRepository.findAll(pageable);
		}
		return applications.map(this::convertToDTO);
	}

	@Override
	public ApplicationDTO getApplication(Long id) {
		Application application = applicationRepository.findById(id)
				.orElseThrow(() -> new ApplicationNotFoundException(id));
		return convertToDTO(application);
	}

	@Override
	public ApplicationDTO addApplication(ApplicationDTO applicationDTO) {
		if (applicationDTO.getTitle() == null || applicationDTO.getTitle().isEmpty() ||
				applicationDTO.getDescription() == null || applicationDTO.getDescription().isEmpty()) {
			throw new IncorrectParamException("Title and Description is required");
		}

		Application application = new Application();
		State createdState = stateRepository.findById(StateEnum.CREATED.getId())
				.orElseThrow(StateNotFoundException::new);

		application.setState(createdState);
		application.setTitle(applicationDTO.getTitle());
		application.setDescription(applicationDTO.getDescription());
		Application savedApplication = applicationRepository.save(application);
		applicationChangeService.logApplicationChange(savedApplication);
		return convertToDTO(savedApplication);
	}

	@Override
	public ApplicationDTO updateApplication(Long id, String description) {

		Application application = applicationRepository.findById(id)
				.orElseThrow(() -> new ApplicationNotFoundException(id));

		State applicationState = application.getState();
		if (Objects.equals(applicationState.getId(), StateEnum.CREATED.getId()) || Objects.equals(applicationState.getId(), StateEnum.VERIFIED.getId())) {
			applicationChangeService.logApplicationChange(application);
			application.setDescription(description);
			application = applicationRepository.save(application);
		} else {
			throw new IncorrectStateException("Application description can be updated only on CREATED and VERIFIED states.");
		}
		return convertToDTO(application);
	}

	@Override
	public ApplicationDTO verifyApplication(Long id) {
		return changeState(id, StateEnum.CREATED, StateEnum.VERIFIED);
	}

	@Override
	public ApplicationDTO acceptApplication(Long id) {
		return changeState(id, StateEnum.VERIFIED, StateEnum.ACCEPTED);
	}

	@Override
	public ApplicationDTO publishApplication(Long id) {
		return changeState(id, StateEnum.ACCEPTED, StateEnum.PUBLISHED);
	}

	@Override
	public void deleteApplication(Long id, String reason) {
		List<Long> requiredStatedIdList = Collections.singletonList(StateEnum.CREATED.getId());
		deleteOrRejectApplication(id, requiredStatedIdList, StateEnum.DELETED, reason);
	}

	@Override
	public ApplicationDTO rejectApplication(Long id, String reason) {
		List<Long> requiredStatedIdList = Arrays.asList(StateEnum.VERIFIED.getId(), StateEnum.ACCEPTED.getId());
		return deleteOrRejectApplication(id, requiredStatedIdList, StateEnum.REJECTED, reason);
	}

	private ApplicationDTO changeState(Long id, StateEnum oldState, StateEnum newState) {
		Application application = applicationRepository.findById(id)
				.orElseThrow(() -> new ApplicationNotFoundException(id));

		if (Objects.equals(application.getState().getId(), oldState.getId())) {
			State wantedState = stateRepository.findById(newState.getId())
					.orElseThrow(StateNotFoundException::new);
			applicationChangeService.logApplicationChange(application, "Changed application state for " + newState.name());
			application.setState(wantedState);
			Application updatedApplication = applicationRepository.save(application);

			return convertToDTO(updatedApplication);
		} else {
			throw new IncorrectStateException("Invalid state transition from " + application.getState().getName() + " to " + newState.name());
		}
	}

	private ApplicationDTO deleteOrRejectApplication(Long id, List<Long> oldStateIdList, StateEnum newState, String reason) {
		if (reason.isEmpty()) {
			throw new IncorrectParamException("Reason is required for change application state on " + newState.name());
		}

		Application application = applicationRepository.findById(id)
				.orElseThrow(() -> new ApplicationNotFoundException(id));

		if (oldStateIdList.contains(application.getState().getId())) {
			State wantedState = stateRepository.findById(newState.getId())
					.orElseThrow(StateNotFoundException::new);
			applicationChangeService.logApplicationChange(application, "Application was " + newState.name() + " by reason: " + reason);
			application.setState(wantedState);
			Application updatedApplication = applicationRepository.save(application);

			return convertToDTO(updatedApplication);
		} else {
			throw new IncorrectStateException("Invalid state transition from " + application.getState().getName() + " to " + newState.name());
		}
	}

	ApplicationDTO convertToDTO(Application application) {
		ApplicationDTO dto = new ApplicationDTO();
		dto.setId(application.getId());
		dto.setTitle(application.getTitle());
		dto.setDescription(application.getDescription());
		dto.setState(application.getState().getName());
		return dto;
	}

}
