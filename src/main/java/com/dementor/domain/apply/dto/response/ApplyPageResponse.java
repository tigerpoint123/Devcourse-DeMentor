package com.dementor.domain.apply.dto.response;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.dementor.domain.apply.entity.Apply;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyPageResponse {
	private List<ApplyDetailResponse> applyments;
	private Map<String, Object> pagination;

	public static ApplyPageResponse from(Page<Apply> page, int pageNum, int size) {
		return ApplyPageResponse.builder()
			.applyments(page.map(ApplyDetailResponse::from).getContent())
			.pagination(Map.of("page", pageNum + 1, "size", size,
				"total_elements", page.getTotalElements(),
				"total_pages", page.getTotalPages()))
			.build();
	}
}
