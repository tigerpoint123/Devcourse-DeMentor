package com.dementor.domain.admin;

import com.dementor.domain.admin.dto.wtf.ApplymentDetailResponse;
import com.dementor.domain.admin.dto.wtf.ApplymentResponse;
import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.admin.repository.AdminMentorApplymentRepository;
import com.dementor.domain.job.entity.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMentorApplymentService {
    private final AdminMentorApplymentRepository adminMentorApplymentRepository;

    public Page<ApplymentResponse> findAllApplyment(Pageable pageable) {
        return adminMentorApplymentRepository.findAllWithJob(pageable)
                .map(tuple -> {
                    AdminMentorApplyment applyment = tuple.get(0, AdminMentorApplyment.class);
                    Job job = tuple.get(1, Job.class);

                    return ApplymentResponse.from(applyment, job);
                });
    }

    public ApplymentDetailResponse findOneApplyment(Long memberId) {
        return null;
    }
}
