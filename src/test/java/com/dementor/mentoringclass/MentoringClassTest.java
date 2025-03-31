package com.dementor.mentoringclass;

import com.dementor.config.TestSecurityConfig;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.ScheduleRequest;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
public class MentoringClassTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MentoringClassRepository mentoringClassRepository;

    private Long testClassId;

    @BeforeEach
    void 셋업() {
        // 테스트 데이터 생성
        MentoringClass mentoringClass = MentoringClass.builder()
                .title("테스트 수업")
                .stack("Spring Boot")
                .content("테스트용 수업 내용")
                .price(50000)
                .build();

        mentoringClass = mentoringClassRepository.save(mentoringClass);

        testClassId = mentoringClass.getId();
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        mentoringClassRepository.deleteAll();
    }

    @Test
    void 멘토링수업전체조회() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/class")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("멘토링 수업 조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void 멘토링수업상세조회() throws Exception {
        //when
        mockMvc.perform(get("/api/class/{class_id}", testClassId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("멘토링 수업 상세 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(testClassId))  // 단일 객체 검증
                .andExpect(jsonPath("$.data.title").value("테스트 수업"))
                .andExpect(jsonPath("$.data.stack").value("Spring Boot"))
                .andExpect(jsonPath("$.data.content").value("테스트용 수업 내용"))
                .andExpect(jsonPath("$.data.price").value(50000));
        //then
    }

    @Test
    @WithMockUser(roles = "MENTOR")
    void 멘토링수업등록() throws Exception {
        // given
        MentoringClassCreateRequest request = new MentoringClassCreateRequest(
            "Spring Boot",
            "스프링 부트 기초부터 실전까지",
            "스프링 부트 완전 정복",
            50000,
            List.of(
                new ScheduleRequest("월요일", 10001100)
            )
        );

        // when & then
        mockMvc.perform(post("/api/class")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("멘토링 클래스 생성 성공"))
                .andExpect(jsonPath("$.data").isNumber());
    }

}
