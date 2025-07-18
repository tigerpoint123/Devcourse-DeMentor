package com.dementor.notification;

import com.dementor.config.TestSecurityConfig;
import com.dementor.domain.admin.entity.Admin;
import com.dementor.domain.admin.repository.AdminRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.notification.entity.FailedNotification;
import com.dementor.domain.notification.entity.Notification;
import com.dementor.domain.notification.entity.NotificationType;
import com.dementor.domain.notification.entity.ErrorType;
import com.dementor.domain.notification.repository.NotificationRepository;
import com.dementor.domain.notification.repository.FailedNotificationRepository;
import com.dementor.global.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
public class NotificationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FailedNotificationRepository failedNotificationRepository;

    private Long testNotificationId;
    private Long testFailedNotificationId;
    private CustomUserDetails userPrincipal;

    @BeforeEach
    void setUp() {
        // 테스트용 ADMIN 사용자 생성
        Admin admin = Admin.builder()
                .username("testadmin")
                .password("password")
                .build();
        admin = adminRepository.save(admin);
        userPrincipal = CustomUserDetails.ofAdmin(admin);

        // 테스트용 일반 사용자 생성 (알림 수신자용)
        Member member = Member.builder()
                .email("test@test.com")
                .password("password")
                .nickname("테스트 사용자")
                .name("테스트 사용자")
                .userRole(UserRole.MENTOR)
                .build();
        member = memberRepository.save(member);

        // 테스트용 알림 생성
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("mentorId", 1L);
        notificationData.put("classId", 1L);

        Notification notification = Notification.builder()
                .receiver(member)
                .type(NotificationType.MENTORING_APPLY)
                .content("새로운 멘토링 신청이 있습니다.")
                .data(notificationData)
                .build();
        notification = notificationRepository.save(notification);
        testNotificationId = notification.getId();

        // 테스트용 실패 알림 생성
        FailedNotification failedNotification = FailedNotification.builder()
                .receiver(member)
                .type(NotificationType.MENTORING_APPLY)
                .content("실패한 알림 테스트")
                .data(notificationData)
                .errorMessage("네트워크 오류")
                .retried(false)
                .retryCount(0)
                .errorType(ErrorType.NETWORK)
                .build();
        failedNotification = failedNotificationRepository.save(failedNotification);
        testFailedNotificationId = failedNotification.getId();

        // Security Context에 인증 정보 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getNotifications() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/notifications")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("order", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("알림 조회 성공"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber())
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    void getUnreadNotifications() throws Exception {
        // when & then
        mockMvc.perform(get("/api/notifications/unread")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("읽지 않은 알림 조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getUnreadCount() throws Exception {
        // when & then
        mockMvc.perform(get("/api/notifications/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("안 읽은 알림 개수 조회 성공"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void markAsRead() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/notifications/{notificationId}/read", testNotificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.message").value("읽기 처리 성공"));
    }

    @Test
    void retryFailedNotification() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/dlq/notifications/{id}/retry", testFailedNotificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("재처리 성공"))
                .andExpect(jsonPath("$.data.success").value(false))
                .andExpect(jsonPath("$.data.notificationId").value(testFailedNotificationId));
    }

    @Test
    void markAsReadWithInvalidNotificationId() throws Exception {
        // given
        Long invalidNotificationId = 99999L;

        // when & then
        mockMvc.perform(patch("/api/notifications/{notificationId}/read", invalidNotificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("알림을 찾을 수 없습니다."));
    }

    @Test
    void retryFailedNotificationWithInvalidId() throws Exception {
        // given
        Long invalidFailedNotificationId = 99999L;

        if(testFailedNotificationId.equals(invalidFailedNotificationId)) System.out.println("equal");
        // when & then
        mockMvc.perform(patch("/api/dlq/notifications/{id}/retry", invalidFailedNotificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.message").value("실패 알림 조회 실패"));
    }
} 