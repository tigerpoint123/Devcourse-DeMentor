package com.dementor.favorite;

import com.dementor.config.TestSecurityConfig;
import com.dementor.domain.favorite.entity.Favorite;
import com.dementor.domain.favorite.repository.FavoriteRepository;
import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.entity.ModificationStatus;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.global.security.CustomUserDetails;
import com.dementor.global.security.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, WebConfig.class})
@Transactional
public class FavoriteTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MentoringClassRepository mentoringClassRepository;
    @Autowired
    private JobRepository jobRepository;

    private Member mentee;
    private Member mentor;
    private Mentor testMentor;
    private MentoringClass mentoringClass;
    private CustomUserDetails menteePrincipal;

    @BeforeEach
    void setUp() {
        Job job = jobRepository.save(
                Job.builder()
                        .name("백엔드 개발자")
                        .build()
        );

        mentee = memberRepository.save(
                Member.builder()
                        .email("mentee1@test.com")
                        .password("1234")
                        .name("TEST_NAME")
                        .nickname("TEST_NAME")
                        .userRole(UserRole.MENTEE)
                        .build()
        );

        mentor = memberRepository.save(
                Member.builder()
                        .email("test@test.com")
                        .password("password")
                        .nickname("테스트 멘토")
                        .name("테스트 멘토")
                        .userRole(UserRole.MENTOR)
                        .build()
        );
        menteePrincipal = CustomUserDetails.of(mentee);

        testMentor = mentorRepository.save(
                Mentor.builder()
                        .member(mentor)
                        .job(job)
                        .name("테스트 멘토")
                        .currentCompany("테스트 회사")
                        .career(5)
                        .phone("010-1234-5678")
                        .email("mentor@example.com")
                        .introduction("테스트 멘토 소개")
                        .modificationStatus(ModificationStatus.NONE)
                        .build()
        );

        mentoringClass = mentoringClassRepository.save(
                MentoringClass.builder()
                        .title("TEST_TITLE")
                        .stack("Java,Spring")
                        .content("TEST_CONTENT")
                        .price(10000)
                        .build()
        );

        // Security Context에 인증 정보 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                menteePrincipal,
                null,
                menteePrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("회원의 즐겨찾기 등록")
    void addFavorite() throws Exception {
        //given
        Long memberId = mentee.getId();
        Long classId = mentoringClass.getId();

        //when & then
        mockMvc.perform(post("/api/favorite/{classId}", classId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("즐겨찾기 등록 성공"))
                .andExpect(jsonPath("$.data.favoriteId").exists())
                .andExpect(jsonPath("$.data.mentoringClassId").value(classId))
                .andExpect(jsonPath("$.data.memberId").value(memberId));
    }

    @Test
    @DisplayName("회원의 즐겨찾기 삭제")
    void deleteFavorite() throws Exception {
        //given
        Long memberId = mentee.getId();
        Long classId = mentoringClass.getId();
        
        Favorite favorite = favoriteRepository.save(
                Favorite.builder()
                        .mentoringClassId(classId)
                        .memberId(memberId)
                        .build()
        );
        
        //when & then
        mockMvc.perform(delete("/api/favorite/{favoriteId}", favorite.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("즐겨찾기 삭제 성공"));
        
    }

    @Test
    @DisplayName("회원의 즐겨찾기 목록 조회")
    void findAllFavorites() throws Exception {
        //given
        Long memberId = mentee.getId();
        Long classId = mentoringClass.getId();

        Favorite favorite = favoriteRepository.save(
                Favorite.builder()
                        .mentoringClassId(classId)
                        .memberId(memberId)
                        .build()
        );

        //when & then
        mockMvc.perform(get("/api/favorite/{memberId}", memberId)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("favorited"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(favorite.getId()))
                .andExpect(jsonPath("$.data.content[0].mentoringClassId").value(classId))
                .andExpect(jsonPath("$.data.content[0].memberId").value(memberId))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    @DisplayName("이미 등록된 즐겨찾기 중복 등록 시도")
    void addDuplicateFavorite() throws Exception {
        //given
        Long memberId = mentee.getId();
        Long classId = mentoringClass.getId();
        
        // 첫 번째 즐겨찾기 등록
        mockMvc.perform(post("/api/favorite/{classId}", classId))
                .andExpect(status().isOk());
        
        //when & then
        mockMvc.perform(post("/api/favorite/{classId}", classId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("이미 즐겨찾기에 등록된 클래스입니다."));
    }
}
