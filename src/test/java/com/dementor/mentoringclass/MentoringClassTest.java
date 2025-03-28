package com.dementor.mentoringclass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MentoringClassTest {
    @Autowired
    private MockMvc mockMvc;

//    @Test
//    void 멘토링수업전체조회() throws Exception {
//        // given
//        // when & then
//        mockMvc.perform(get("/api/class")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("멘토링 수업 조회 성공"))
//                .andExpect(jsonPath("$.data").isArray());
//    }



}
