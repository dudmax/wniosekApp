package com.example.wniosekapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.wniosekapp.exception.ApplicationNotFoundException;
import com.example.wniosekapp.exception.IncorrectParamException;
import com.example.wniosekapp.exception.IncorrectStateException;
import com.example.wniosekapp.exception.StateNotFoundException;
import com.example.wniosekapp.model.dto.ApplicationDTO;
import com.example.wniosekapp.service.ApplicationService;

@RestController
@RequestMapping("/applications")
class ApplicationController {

	private final ApplicationService applicationService;

	@Autowired
	public ApplicationController(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@GetMapping
	public ResponseEntity<Page<ApplicationDTO>> getApplications(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String state) {
		try {
			return ResponseEntity.ok(applicationService.getApplications(page, size, name, state));
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		}
}

	@GetMapping("/{id}")
	public ResponseEntity<ApplicationDTO> getApplication(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(applicationService.getApplication(id));
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<ApplicationDTO> addApplication(@RequestBody ApplicationDTO applicationDTO) {
		try {
			return ResponseEntity.ok(applicationService.addApplication(applicationDTO));
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		}
		catch (IncorrectParamException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApplicationDTO> updateApplication(@PathVariable Long id, @RequestBody String description) {
		try {
			return ResponseEntity.ok(applicationService.updateApplication(id, description));
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IncorrectStateException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteApplication(@PathVariable Long id, @RequestParam String reason) {
		try {
			applicationService.deleteApplication(id, reason);
			return ResponseEntity.noContent().build();
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IncorrectStateException | IncorrectParamException e) {
			return ResponseEntity.badRequest().build();
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/{id}/verify")
	public ResponseEntity<ApplicationDTO> verifyApplication(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(applicationService.verifyApplication(id));
		} catch (IncorrectStateException e) {
			return ResponseEntity.badRequest().build();
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/{id}/accept")
	public ResponseEntity<ApplicationDTO> acceptApplication(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(applicationService.acceptApplication(id));
		} catch (IncorrectStateException e) {
			return ResponseEntity.badRequest().build();
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/{id}/publish")
	public ResponseEntity<ApplicationDTO> publishApplication(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(applicationService.publishApplication(id));
		} catch (IncorrectStateException | IncorrectParamException e) {
			return ResponseEntity.badRequest().build();
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<ApplicationDTO> rejectApplication(@PathVariable Long id, @RequestParam String reason) {
		try {
			return ResponseEntity.ok(applicationService.rejectApplication(id, reason));
		} catch (ApplicationNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IncorrectStateException e) {
			return ResponseEntity.badRequest().build();
		} catch (StateNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
