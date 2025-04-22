package com.dementor.domain.mentor.dto.applyment.response;

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
