package com.dementor.global.swaggerDocs;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassUpdateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassDetailResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassUpdateResponse;
import com.dementor.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

/*
 * TODO :
 *  1. 멘토링 클래스 조회 캐싱
 *  2. 캐시 만료 전략
 *  3. 캐시 키 관리
 *
 테스트 시나리오
1. 즐겨찾기 추가/삭제 동시성 테스트
   - 여러 사용자가 동시에 같은 클래스에 즐겨찾기 추가/삭제
   - Redis 카운터의 정확성 검증

2. 인기 클래스 조회 부하 테스트
   - 즐겨찾기 수가 많은 클래스들의 조회 요청
   - 캐시 히트율 측정

3. 캐시 동기화 테스트
   - 5분 주기 동기화 중에 발생하는 부하
   - DB와 Redis 간의 데이터 일관성 검증
 * */
@Tag(name = "멘토링 수업", description = "멘토링 수업 관리")
public interface MentoringClassSwagger {
	@Operation(summary = "멘토링 수업 전체 조회", description = "모든 멘토링 수업을 조회합니다.")
	ResponseEntity<ApiResponse<Page<MentoringClassFindResponse>>> getClass(
		List<String> jobId,
		@Parameter(description = "페이지 정보", example = """
			{
			  "page": 1,
			  "size": 10,
			  "sort": "id,desc"
			}
			""") Pageable pageable);

	@Operation(summary = "멘토링 수업 상세 조회", description = "특정 멘토링 수업의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<MentoringClassDetailResponse>> getClassById(Long classId);

	@Operation(summary = "멘토링 수업 등록", description = "멘토가 멘토링 수업을 등록합니다.")
	ResponseEntity<ApiResponse<MentoringClassDetailResponse>> createClass(
		MentoringClassCreateRequest request,
		Authentication authentication);

	@Operation(summary = "멘토링 수업 수정", description = "멘토링 수업 정보를 수정합니다.")
	ResponseEntity<ApiResponse<MentoringClassUpdateResponse>> updateClass(
		Long classId,
		MentoringClassUpdateRequest request,
		Authentication authentication);

	@Operation(summary = "멘토링 수업 삭제", description = "멘토링 수업을 삭제합니다.")
	ResponseEntity<ApiResponse<?>> deleteClass(Long classId);

	@Operation(summary = "멘토링 즐겨찾기 개수", description = "멘토링 즐겨찾기 개수를 조회합니다.")
	ResponseEntity<ApiResponse<Integer>> findFavoriteCount(Long classId);
}
