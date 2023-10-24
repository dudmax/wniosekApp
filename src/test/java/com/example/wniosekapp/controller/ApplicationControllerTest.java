package com.example.wniosekapp.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.example.wniosekapp.exception.ApplicationNotFoundException;
import com.example.wniosekapp.exception.IncorrectParamException;
import com.example.wniosekapp.exception.IncorrectStateException;
import com.example.wniosekapp.exception.StateNotFoundException;
import com.example.wniosekapp.model.dto.ApplicationDTO;
import com.example.wniosekapp.service.ApplicationService;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	private ApplicationController applicationController;

	@Mock
	private ApplicationService applicationService;

	@Test
	void testGetApplications() {
		// Arrange
		Page<ApplicationDTO> mockPage = mock(Page.class);
		when(applicationService.getApplications(eq(0), eq(10), eq(null), eq(null)))
				.thenReturn(mockPage);

		// Act
		ResponseEntity<Page<ApplicationDTO>> response = applicationController.getApplications(0, 10, null, null);

		// Assert
		verify(applicationService).getApplications(0, 10, null, null);
		verifyNoMoreInteractions(applicationService);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(mockPage, response.getBody());
	}

	@Test
	void testGetApplicationsWithStateNotFoundException() {
		// Arrange
		when(applicationService.getApplications(0, 10, null, null))
				.thenThrow(new StateNotFoundException());

		// Act
		ResponseEntity<Page<ApplicationDTO>> response = applicationController.getApplications(0, 10, null, null);

		// Assert
		verify(applicationService).getApplications(0, 10, null, null);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testGetApplication() {
		// Arrange
		Long applicationId = 1L;
		ApplicationDTO mockApplicationDTO = mock(ApplicationDTO.class);
		when(applicationService.getApplication(applicationId)).thenReturn(mockApplicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.getApplication(applicationId);

		// Assert
		verify(applicationService).getApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(mockApplicationDTO, response.getBody());
	}

	@Test
	void testGetApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.getApplication(applicationId))
				.thenThrow(new ApplicationNotFoundException(applicationId));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.getApplication(applicationId);

		// Assert
		verify(applicationService).getApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testAddApplication() {
		// Arrange
		ApplicationDTO applicationDTO = mock(ApplicationDTO.class);
		when(applicationService.addApplication(applicationDTO))
				.thenReturn(applicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.addApplication(applicationDTO);

		// Assert
		verify(applicationService).addApplication(applicationDTO);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(applicationDTO, response.getBody());
	}

	@Test
	void testAddApplicationWithStatusNotFoundException() {
		// Arrange
		ApplicationDTO applicationDTO = mock(ApplicationDTO.class);
		when(applicationService.addApplication(applicationDTO))
				.thenThrow(new StateNotFoundException());

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.addApplication(applicationDTO);

		// Assert
		verify(applicationService).addApplication(applicationDTO);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testAddApplicationWithIncorrectParamException() {
		// Arrange
		ApplicationDTO applicationDTO = mock(ApplicationDTO.class);
		when(applicationService.addApplication(applicationDTO))
				.thenThrow(new IncorrectParamException("Incorrect param"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.addApplication(applicationDTO);

		// Assert
		verify(applicationService).addApplication(applicationDTO);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testUpdateApplication() {
		// Arrange
		Long applicationId = 1L;
		String description = "Updated description";
		ApplicationDTO mockApplicationDTO = mock(ApplicationDTO.class);
		when(applicationService.updateApplication(applicationId, description))
				.thenReturn(mockApplicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.updateApplication(applicationId, description);

		// Assert
		verify(applicationService).updateApplication(applicationId, description);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(mockApplicationDTO, response.getBody());
	}

	@Test
	void testUpdateApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		String description = "Updated description";
		when(applicationService.updateApplication(applicationId, description))
				.thenThrow(new ApplicationNotFoundException(applicationId));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.updateApplication(applicationId, description);

		// Assert
		verify(applicationService).updateApplication(applicationId, description);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testUpdateApplicationWithIncorrectStateException() {
		// Arrange
		Long applicationId = 1L;
		String description = "Updated description";
		when(applicationService.updateApplication(applicationId, description))
				.thenThrow(new IncorrectStateException("Incorrect state"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.updateApplication(applicationId, description);

		// Assert
		verify(applicationService).updateApplication(applicationId, description);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testDeleteApplication() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Deleted";

		// Act
		ResponseEntity<Object> response = applicationController.deleteApplication(applicationId, reason);

		// Assert
		verify(applicationService).deleteApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void testDeleteApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Deleted";
		doThrow(new ApplicationNotFoundException(applicationId))
				.when(applicationService).deleteApplication(applicationId, reason);

		// Act
		ResponseEntity<Object> response = applicationController.deleteApplication(applicationId, reason);

		// Assert
		verify(applicationService).deleteApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testDeleteApplicationWithIncorrectStateException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Deleted";
		doThrow(new IncorrectStateException("Incorrect state"))
				.when(applicationService).deleteApplication(applicationId, reason);

		// Act
		ResponseEntity<Object> response = applicationController.deleteApplication(applicationId, reason);

		// Assert
		verify(applicationService).deleteApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testDeleteApplicationWithIncorrectParamException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Deleted";
		doThrow(new IncorrectParamException("Incorrect parameter"))
				.when(applicationService).deleteApplication(applicationId, reason);

		// Act
		ResponseEntity<Object> response = applicationController.deleteApplication(applicationId, reason);

		// Assert
		verify(applicationService).deleteApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testDeleteApplicationWithStateNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Deleted";
		doThrow(new StateNotFoundException())
				.when(applicationService).deleteApplication(applicationId, reason);

		// Act
		ResponseEntity<Object> response = applicationController.deleteApplication(applicationId, reason);

		// Assert
		verify(applicationService).deleteApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testVerifyApplication() {
		// Arrange
		Long applicationId = 1L;
		ApplicationDTO mockApplicationDTO = mock(ApplicationDTO.class);
		when(applicationService.verifyApplication(applicationId))
				.thenReturn(mockApplicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.verifyApplication(applicationId);

		// Assert
		verify(applicationService).verifyApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(mockApplicationDTO, response.getBody());
	}

	@Test
	void testVerifyApplicationWithIncorrectStateException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.verifyApplication(applicationId))
				.thenThrow(new IncorrectStateException("Incorrect state"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.verifyApplication(applicationId);

		// Assert
		verify(applicationService).verifyApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testVerifyApplicationWithStateNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.verifyApplication(applicationId))
				.thenThrow(new StateNotFoundException());

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.verifyApplication(applicationId);

		// Assert
		verify(applicationService).verifyApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testVerifyApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.verifyApplication(applicationId))
				.thenThrow(new ApplicationNotFoundException(applicationId));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.verifyApplication(applicationId);

		// Assert
		verify(applicationService).verifyApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testAcceptApplication() {
		// Arrange
		Long applicationId = 1L;
		ApplicationDTO mockApplicationDTO = mock(ApplicationDTO.class);
		when(applicationService.acceptApplication(applicationId))
				.thenReturn(mockApplicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.acceptApplication(applicationId);

		// Assert
		verify(applicationService).acceptApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(mockApplicationDTO, response.getBody());
	}

	@Test
	void testAcceptApplicationWithIncorrectStateException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.acceptApplication(applicationId))
				.thenThrow(new IncorrectStateException("Incorrect state"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.acceptApplication(applicationId);

		// Assert
		verify(applicationService).acceptApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testAcceptApplicationWithStateNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.acceptApplication(applicationId))
				.thenThrow(new StateNotFoundException());

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.acceptApplication(applicationId);

		// Assert
		verify(applicationService).acceptApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testAcceptApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.acceptApplication(applicationId))
				.thenThrow(new ApplicationNotFoundException(applicationId));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.acceptApplication(applicationId);

		// Assert
		verify(applicationService).acceptApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testPublishApplication() {
		// Arrange
		Long applicationId = 1L;
		ApplicationDTO mockApplicationDTO = mock(ApplicationDTO.class);
		when(applicationService.publishApplication(applicationId))
				.thenReturn(mockApplicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.publishApplication(applicationId);

		// Assert
		verify(applicationService).publishApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(mockApplicationDTO, response.getBody());
	}

	@Test
	void testPublishApplicationWithIncorrectStateException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.publishApplication(applicationId))
				.thenThrow(new IncorrectStateException("Incorrect state"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.publishApplication(applicationId);

		// Assert
		verify(applicationService).publishApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testPublishApplicationWithIncorrectParamException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.publishApplication(applicationId))
				.thenThrow(new IncorrectParamException("Incorrect parameter"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.publishApplication(applicationId);

		// Assert
		verify(applicationService).publishApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testPublishApplicationWithStateNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.publishApplication(applicationId))
				.thenThrow(new StateNotFoundException());

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.publishApplication(applicationId);

		// Assert
		verify(applicationService).publishApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testPublishApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		when(applicationService.publishApplication(applicationId))
				.thenThrow(new ApplicationNotFoundException(applicationId));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.publishApplication(applicationId);

		// Assert
		verify(applicationService).publishApplication(applicationId);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testRejectApplication() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Rejected";
		ApplicationDTO mockApplicationDTO = mock(ApplicationDTO.class);
		when(applicationService.rejectApplication(applicationId, reason))
				.thenReturn(mockApplicationDTO);

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.rejectApplication(applicationId, reason);

		// Assert
		verify(applicationService).rejectApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertSame(mockApplicationDTO, response.getBody());
	}

	@Test
	void testRejectApplicationWithApplicationNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Rejected";
		when(applicationService.rejectApplication(applicationId, reason))
				.thenThrow(new ApplicationNotFoundException(applicationId));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.rejectApplication(applicationId, reason);

		// Assert
		verify(applicationService).rejectApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testRejectApplicationWithIncorrectStateException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Rejected";
		when(applicationService.rejectApplication(applicationId, reason))
				.thenThrow(new IncorrectStateException("Incorrect state"));

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.rejectApplication(applicationId, reason);

		// Assert
		verify(applicationService).rejectApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testRejectApplicationWithStateNotFoundException() {
		// Arrange
		Long applicationId = 1L;
		String reason = "Rejected";
		when(applicationService.rejectApplication(applicationId, reason))
				.thenThrow(new StateNotFoundException());

		// Act
		ResponseEntity<ApplicationDTO> response = applicationController.rejectApplication(applicationId, reason);

		// Assert
		verify(applicationService).rejectApplication(applicationId, reason);
		verifyNoMoreInteractions(applicationService);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}
}
