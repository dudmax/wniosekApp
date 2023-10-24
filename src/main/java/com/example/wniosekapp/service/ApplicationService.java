package com.example.wniosekapp.service;

import org.springframework.data.domain.Page;
import com.example.wniosekapp.model.dto.ApplicationDTO;

public interface ApplicationService {

	Page<ApplicationDTO> getApplications(int page, int size, String name, String state);

	ApplicationDTO getApplication(Long id);

	ApplicationDTO addApplication(ApplicationDTO applicationDTO);

	ApplicationDTO updateApplication(Long id, String description);

	ApplicationDTO verifyApplication(Long id);

	ApplicationDTO acceptApplication(Long id);

	ApplicationDTO publishApplication(Long id);

	void deleteApplication(Long id, String reason);

	ApplicationDTO rejectApplication(Long appId, String reason);
}
