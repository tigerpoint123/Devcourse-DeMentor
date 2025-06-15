package com.dementor.domain.elasticsearch.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class JobInfo {
    private Long id;
    private String name;

    public static JobInfo from(com.dementor.domain.job.entity.Job job) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setId(job.getId());
        jobInfo.setName(job.getName());
        return jobInfo;
    }
}
