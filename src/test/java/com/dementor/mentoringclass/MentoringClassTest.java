package com.dementor.mentoringclass;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // 테스트 프로필 활성화
public class MentoringClassTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void 멘토링수업전체조회() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/class")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("멘토링 수업 조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "MENTOR") // 멘토 역할을 가진 사용자로 인증
    void 멘토링수업등록() throws Exception {
        // given
        MentoringClassCreateRequest request = new MentoringClassCreateRequest(
            1L, // 임시 mentorId
            "Spring Boot",
            "스프링 부트 기초부터 실전까지",
            "스프링 부트 완전 정복",
            50000
        );

        // when & then
        mockMvc.perform(post("/api/class")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("멘토링 클래스 생성 성공"))
                .andExpect(jsonPath("$.data").isNumber());
    }

}
