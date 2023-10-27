package com.example.wniosekapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.wniosekapp.exception.IncorrectParamException;
import com.example.wniosekapp.exception.IncorrectStateException;
import com.example.wniosekapp.model.Application;
import com.example.wniosekapp.model.State;
import com.example.wniosekapp.model.dto.ApplicationDTO;
import com.example.wniosekapp.repository.ApplicationRepository;
import com.example.wniosekapp.repository.StateRepository;
import com.example.wniosekapp.service.ApplicationChangeService;
import com.example.wniosekapp.service.util.StateEnum;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {

	@InjectMocks
	private ApplicationServiceImpl applicationService;

	@Mock
	private ApplicationRepository applicationRepository;

	@Mock
	private StateRepository stateRepository;

	@Test
	public void testGetApplications() {
		// Define test data and expectations
		Pageable pageable = PageRequest.of(0, 10);
		State state = new State(StateEnum.CREATED);
		when(stateRepository.findByName("CREATED")).thenReturn(state);

		List<Application> applications = new ArrayList<>();
		Application application = new Application();
		application.setTitle("TestName");
		application.setDescription("Test description");
		application.setState(new State(StateEnum.CREATED));
		applications.add(application);
		Page<Application> applicationPage = new PageImpl<>(applications);
		when(applicationRepository.findByStateAndTitleContaining(state, "TestName", pageable)).thenReturn(applicationPage);

		// Call the service method
		Page<ApplicationDTO> result = applicationService.getApplications(0, 10, "TestName", "CREATED");

		// Assert the result
		ApplicationDTO applicationDTO = new ApplicationDTO();
		applicationDTO.setTitle("TestName");
		applicationDTO.setDescription("Test description");
		applicationDTO.setState("CREATED");

		List<ApplicationDTO> expectedList = Collections.singletonList(applicationDTO);
		List<ApplicationDTO> resultContent = result.getContent();

		// Assert the result
		assertEquals(expectedList.size(), resultContent.size());
		for (int i = 0; i < expectedList.size(); i++) {
			ApplicationDTO expectedItem = expectedList.get(i);
			ApplicationDTO resultItem = resultContent.get(i);

			// Compare properties of expectedItem and resultItem here
			assertEquals(expectedItem.getTitle(), resultItem.getTitle());
			assertEquals(expectedItem.getDescription(), resultItem.getDescription());
		}
	}

	@Test
	public void testGetApplication() {
		// Arrange
		Long applicationId = 1L;
		Application expectedApplication = new Application();
		expectedApplication.setTitle("TestTitle");
		expectedApplication.setDescription("TestDescription");
		expectedApplication.setState(new State(StateEnum.CREATED));

		when(applicationRepository.findById(applicationId))
				.thenReturn(Optional.of(expectedApplication));

		// Act
		ApplicationDTO result = applicationService.getApplication(applicationId);

		// Assert
		assertNotNull(result);
		assertEquals(expectedApplication.getTitle(), result.getTitle());
		assertEquals(expectedApplication.getDescription(), result.getDescription());
		assertEquals(expectedApplication.getState().getName(), result.getState());
	}

	@Test
	public void testAddApplication() {
		// Define test data and expectations
		ApplicationDTO applicationDTO = new ApplicationDTO();
		applicationDTO.setTitle("New Application");
		applicationDTO.setDescription("Description");

		State createdState = new State();
		when(stateRepository.findById(StateEnum.CREATED.getId())).thenReturn(Optional.of(createdState));

		Application expectedApplication = new Application();
		expectedApplication.setTitle("TestTitle");
		expectedApplication.setDescription("TestDescription");
		expectedApplication.setState(new State(StateEnum.CREATED));
		when(applicationRepository.save(any(Application.class))).thenReturn(expectedApplication);

		// Call the service method
		ApplicationDTO result = applicationService.addApplication(applicationDTO);

		// Assert the result
		assertNotNull(result);
		assertEquals(expectedApplication.getTitle(), result.getTitle());
		assertEquals(expectedApplication.getDescription(), result.getDescription());
		assertEquals(expectedApplication.getState().getName(), result.getState());
	}

	@Test
	public void testUpdateApplication() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.CREATED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
		when(applicationRepository.save(any(Application.class))).thenReturn(application);

		// Call the service method
		ApplicationDTO result = applicationService.updateApplication(applicationId, "Updated Description");

		// Assert the result
		assertEquals("Updated Description", result.getDescription());
	}

	@Test
	public void testUpdateApplicationIncorrectState() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.ACCEPTED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		// Call the service method
		try {
			applicationService.updateApplication(applicationId, "Updated Description");
			fail("Expected IncorrectStateException to be thrown");
		} catch (IncorrectStateException e) {
			// Expected exception
		}
	}

	@Test
	public void testVerifyApplication() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setId(applicationId);
		application.setTitle("TestTitle");
		application.setDescription("TestDescription");
		application.setState(new State(StateEnum.CREATED));
		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		State expectedState = new State(StateEnum.VERIFIED);
		when(stateRepository.findById(StateEnum.VERIFIED.getId())).thenReturn(Optional.of(expectedState));
		when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
			Application savedApplication = invocation.getArgument(0);
			savedApplication.setState(expectedState);
			return savedApplication;
		});


		// Call the service method
		ApplicationDTO result = applicationService.verifyApplication(applicationId);

		// Assert the result
		verify(applicationRepository).save(application);
		assertEquals(StateEnum.VERIFIED.name(), result.getState());
	}

	@Test
	public void testVerifyApplicationIncorrectState() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.PUBLISHED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		// Call the service method
		try {
			applicationService.verifyApplication(applicationId);
			fail("Expected IncorrectStateException to be thrown");
		} catch (IncorrectStateException e) {
			// Expected exception
		}
	}

	@Test
	public void testAcceptApplication() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setId(applicationId);
		application.setTitle("TestTitle");
		application.setDescription("TestDescription");
		application.setState(new State(StateEnum.VERIFIED));
		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		State expectedState = new State(StateEnum.ACCEPTED);
		when(stateRepository.findById(StateEnum.ACCEPTED.getId())).thenReturn(Optional.of(expectedState));
		when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
			Application savedApplication = invocation.getArgument(0);
			savedApplication.setState(expectedState);
			return savedApplication;
		});


		// Call the service method
		ApplicationDTO result = applicationService.acceptApplication(applicationId);

		// Assert the result
		verify(applicationRepository).save(application);
		assertEquals(StateEnum.ACCEPTED.name(), result.getState());
	}

	@Test
	public void testAcceptApplicationIncorrectState() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.PUBLISHED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		// Call the service method
		try {
			applicationService.acceptApplication(applicationId);
			fail("Expected IncorrectStateException to be thrown");
		} catch (IncorrectStateException e) {
			// Expected exception
		}
	}

	@Test
	public void testPublishApplication() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setId(applicationId);
		application.setTitle("TestTitle");
		application.setDescription("TestDescription");
		application.setState(new State(StateEnum.ACCEPTED));
		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		State expectedState = new State(StateEnum.PUBLISHED);
		when(stateRepository.findById(StateEnum.PUBLISHED.getId())).thenReturn(Optional.of(expectedState));
		when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
			Application savedApplication = invocation.getArgument(0);
			savedApplication.setState(expectedState);
			return savedApplication;
		});

		// Call the service method
		ApplicationDTO result = applicationService.publishApplication(applicationId);

		// Assert the result
		verify(applicationRepository).save(application);
		assertEquals(StateEnum.PUBLISHED.name(), result.getState());
	}

	@Test
	public void testPublishApplicationIncorrectState() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.CREATED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		// Call the service method
		try {
			applicationService.publishApplication(applicationId);
			fail("Expected IncorrectStateException to be thrown");
		} catch (IncorrectStateException e) {
			// Expected exception
		}
	}

	@Test
	public void testDeleteApplication() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setId(applicationId);
		application.setTitle("TestTitle");
		application.setDescription("TestDescription");
		application.setState(new State(StateEnum.CREATED));
		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		State expectedState = new State(StateEnum.DELETED);
		when(stateRepository.findById(StateEnum.DELETED.getId())).thenReturn(Optional.of(expectedState));
		when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
			Application savedApplication = invocation.getArgument(0);
			savedApplication.setState(expectedState);
			return savedApplication;
		});

		// Call the service method
		applicationService.deleteApplication(applicationId, "Test");

		// Assert that the application's state has been set to DELETED
		verify(applicationRepository).save(application);
		assertEquals(StateEnum.DELETED.name(), application.getState().getName());
	}

	@Test
	public void testDeleteApplicationIncorrectState() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.VERIFIED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		// Call the service method
		try {
			applicationService.deleteApplication(applicationId, "Reason for deletion");
			fail("Expected IncorrectStateException to be thrown");
		} catch (IncorrectStateException e) {
			// Expected exception
		}
	}

	@Test
	public void testDeleteApplicationMissingReason() {
		// Define test data and expectations
		Long applicationId = 1L;

		// Call the service method
		try {
			applicationService.deleteApplication(applicationId, "");
			fail("Expected IncorrectParamException to be thrown");
		} catch (IncorrectParamException e) {
			// Expected exception
		}
	}

	@Test
	public void testRejectApplication() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setId(applicationId);
		application.setTitle("TestTitle");
		application.setDescription("TestDescription");
		application.setState(new State(StateEnum.ACCEPTED));
		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		State expectedState = new State(StateEnum.REJECTED);
		when(stateRepository.findById(StateEnum.REJECTED.getId())).thenReturn(Optional.of(expectedState));
		when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
			Application savedApplication = invocation.getArgument(0);
			savedApplication.setState(expectedState);
			return savedApplication;
		});

		// Call the service method
		applicationService.rejectApplication(applicationId, "Test");

		// Assert that the application's state has been set to DELETED
		verify(applicationRepository).save(application);
		assertEquals(StateEnum.REJECTED.name(), application.getState().getName());
	}

	@Test
	public void testRejectApplicationIncorrectState() {
		// Define test data and expectations
		Long applicationId = 1L;
		Application application = new Application();
		application.setState(new State(StateEnum.CREATED));

		when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

		// Call the service method
		try {
			applicationService.rejectApplication(applicationId, "Reason for rejection");
			fail("Expected IncorrectStateException to be thrown");
		} catch (IncorrectStateException e) {
			// Expected exception
		}
	}

	@Test
	public void testRejectApplicationMissingReason() {
		// Define test data and expectations
		Long applicationId = 1L;

		// Call the service method
		try {
			applicationService.rejectApplication(applicationId, "");
			fail("Expected IncorrectParamException to be thrown");
		} catch (IncorrectParamException e) {
			// Expected exception
		}
	}

}
