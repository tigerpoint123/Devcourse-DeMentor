package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.job.entity.Job;

public record JobInfo(
        Long jobId,
        String jobName
) {
    public static JobInfo from(Job job) {
        return new JobInfo(
                job.getId(),
                job.getName()
        );
    }
}
