package com.dementor.domain.admin;

import com.dementor.domain.admin.dto.ApplymentResponse;
import com.dementor.domain.admin.repository.AdminMentorApplymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMentorApplymentService {
    private final AdminMentorApplymentRepository adminMentorApplymentRepository;

    public Page<ApplymentResponse> findAllApplyment(Pageable pageable) {

        return null;
    }
}
