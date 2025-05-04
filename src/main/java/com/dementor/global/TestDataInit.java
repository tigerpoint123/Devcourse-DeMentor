package com.dementor.global;

import com.dementor.domain.admin.entity.Admin;
import com.dementor.domain.admin.repository.AdminRepository;
import com.dementor.domain.apply.repository.ApplyRepository;
import com.dementor.domain.favorite.entity.Favorite;
import com.dementor.domain.favorite.repository.FavoriteRepository;
import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorApplyProposalRepository;
import com.dementor.domain.mentor.repository.MentorEditProposalRepository;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.mentoringclass.dto.DayOfWeek;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.entity.Schedule;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.mentoringclass.repository.ScheduleRepository;
import com.dementor.domain.notification.repository.NotificationRepository;
import com.dementor.domain.postattachment.repository.PostAttachmentRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;

@Component
@Profile("local")
@Slf4j
public class TestDataInit implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobRepository jobRepository;
    private final MentorRepository mentorRepository;
    private final AdminRepository adminRepository;
    private final MentoringClassRepository mentoringClassRepository;
    private final ScheduleRepository scheduleRepository;
    private final MentorApplyProposalRepository mentorApplyProposalRepository;
    private final MentorEditProposalRepository mentorEditProposalRepository;
    private final NotificationRepository notificationRepository;
    private final ApplyRepository applyRepository;
    private final PostAttachmentRepository postAttachmentRepository;
    private final FavoriteRepository favoriteRepository;
    private final Faker faker = new Faker(new Locale("ko"));

    public TestDataInit(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JobRepository jobRepository,
                        MentorRepository mentorRepository, AdminRepository adminRepository,
                        MentoringClassRepository mentoringClassRepository, ScheduleRepository scheduleRepository,
                        MentorApplyProposalRepository mentorApplyProposalRepository,
                        MentorEditProposalRepository mentorEditProposalRepository,
                        NotificationRepository notificationRepository,
                        ApplyRepository applyRepository,
                        PostAttachmentRepository postAttachmentRepository,
                        FavoriteRepository favoriteRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jobRepository = jobRepository;
        this.mentorRepository = mentorRepository;
        this.adminRepository = adminRepository;
        this.mentoringClassRepository = mentoringClassRepository;
        this.scheduleRepository = scheduleRepository;
        this.mentorApplyProposalRepository = mentorApplyProposalRepository;
        this.mentorEditProposalRepository = mentorEditProposalRepository;
        this.notificationRepository = notificationRepository;
        this.applyRepository = applyRepository;
        this.postAttachmentRepository = postAttachmentRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 각 테이블의 데이터 존재 여부 확인
        boolean hasJobs = jobRepository.count() > 0;
        boolean hasAdmin = adminRepository.count() > 0;
        boolean hasMembers = memberRepository.count() > 0;
        boolean hasMentors = mentorRepository.count() > 0;
        boolean hasMentoringClasses = mentoringClassRepository.count() > 0;
        boolean hasSchedules = scheduleRepository.count() > 0;
        boolean hasFavorites = favoriteRepository.count() > 0;

        // 1. 직무 데이터 생성
        List<Job> jobs = new ArrayList<>();
        if (!hasJobs) {
            jobs = createJobs();
            jobRepository.saveAll(jobs);
            log.info("직무 데이터 생성 완료: {}개", jobs.size());
        } else {
            log.info("직무 데이터가 이미 존재합니다.");
            jobs = jobRepository.findAll();
        }

        // 2. 관리자 데이터 생성
        if (!hasAdmin) {
            Admin admin = createAdmin();
            adminRepository.save(admin);
            log.info("관리자 데이터 생성 완료");
        } else {
            log.info("관리자 데이터가 이미 존재합니다.");
        }

        // 3. 멤버 데이터 생성
        List<Member> members = new ArrayList<>();
        if (!hasMembers) {
            members = createMembers();
            memberRepository.saveAll(members);
            log.info("멤버 데이터 생성 완료: {}개", members.size());
        } else {
            log.info("멤버 데이터가 이미 존재합니다.");
            members = memberRepository.findAll();
        }

        // 4. 멘토 데이터 생성
        List<Mentor> mentors = new ArrayList<>();
        if (!hasMentors) {
            mentors = createMentors(members, jobs);
            mentorRepository.saveAll(mentors);
            log.info("멘토 데이터 생성 완료: {}개", mentors.size());
        } else {
            log.info("멘토 데이터가 이미 존재합니다.");
            mentors = mentorRepository.findAll();
        }

        // 5. 멘토링 클래스 데이터 생성
        List<MentoringClass> mentoringClasses = new ArrayList<>();
        if (!hasMentoringClasses) {
            mentoringClasses = createMentoringClasses(mentors);
            mentoringClassRepository.saveAll(mentoringClasses);
            log.info("멘토링 클래스 데이터 생성 완료: {}개", mentoringClasses.size());
        } else {
            log.info("멘토링 클래스 데이터가 이미 존재합니다.");
            mentoringClasses = mentoringClassRepository.findAll();
        }

        // 6. 스케줄 데이터 생성
        if (!hasSchedules) {
            List<Schedule> schedules = createSchedules(mentoringClasses);
            scheduleRepository.saveAll(schedules);
            log.info("스케줄 데이터 생성 완료: {}개", schedules.size());
        } else {
            log.info("스케줄 데이터가 이미 존재합니다.");
        }

        // 7. 즐겨찾기 데이터 생성
        if (!hasFavorites) {
            List<Favorite> favorites = createFavorites(members, mentoringClasses);
            favoriteRepository.saveAll(favorites);
            log.info("즐겨찾기 데이터 생성 완료: {}개", favorites.size());
        } else {
            log.info("즐겨찾기 데이터가 이미 존재합니다.");
        }

        log.info("더미 데이터 생성 완료");
    }

    private List<Job> createJobs() {
        List<Job> jobs = new ArrayList<>();
        String[] jobNames = {"백엔드 개발자", "프론트엔드 개발자", "데이터 엔지니어", "AI 엔지니어", "DevOps 엔지니어"};

        for (String jobName : jobNames) {
            jobs.add(Job.builder()
                    .name(jobName)
                    .build());
        }
        return jobs;
    }

    private Admin createAdmin() {
        if (adminRepository.count() > 0) {
            log.info("관리자 데이터가 이미 존재합니다.");
            return null;
        }

        return Admin.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .build();
    }

    private List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        Set<String> usedNicknames = new HashSet<>();
        Set<String> usedEmails = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            String nickname;
            do {
                nickname = faker.name().username() + "_" + faker.number().digits(4);
            } while (!usedNicknames.add(nickname));

            String email;
            do {
                email = faker.name().firstName().toLowerCase() + 
                       faker.name().lastName().toLowerCase() + 
                       faker.number().digits(4) + 
                       "@" + 
                       faker.internet().domainName();
            } while (!usedEmails.add(email));

            members.add(Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode("1234"))
                    .name(faker.name().fullName())
                    .nickname(nickname)
                    .userRole(UserRole.MENTEE)
                    .build());
        }
        return members;
    }

    private List<Mentor> createMentors(List<Member> members, List<Job> jobs) {
        List<Mentor> mentors = new ArrayList<>();
        Random random = new Random();

        // 멤버 중에서 랜덤하게 30명을 선택하여 멘토로 만듦
        Set<Integer> selectedIndices = new HashSet<>();
        while (selectedIndices.size() < 30) {
            selectedIndices.add(random.nextInt(members.size()));
        }

        for (int index : selectedIndices) {
            Member member = members.get(index);
            Job job = jobs.get(random.nextInt(jobs.size()));

            mentors.add(Mentor.builder()
                    .member(member)
                    .job(job)
                    .name(member.getName())
                    .currentCompany(faker.company().name())
                    .career(random.nextInt(20) + 1)
                    .phone(faker.phoneNumber().cellPhone())
                    .email(member.getEmail())
                    .introduction(faker.lorem().paragraph())
                    .build());

            // 멤버의 역할을 MENTOR로 변경
            member.updateUserRole(UserRole.MENTOR);
        }
        return mentors;
    }

    private List<MentoringClass> createMentoringClasses(List<Mentor> mentors) {
        List<MentoringClass> mentoringClasses = new ArrayList<>();
        String[] techStacks = {"Java", "Spring Boot", "React", "Python", "JavaScript", "TypeScript", "Docker", "Kubernetes"};
        Random random = new Random();

        for (Mentor mentor : mentors) {
            // 각 멘토당 2-3개의 클래스 생성
            int classCount = random.nextInt(2) + 2;
            for (int i = 0; i < classCount; i++) {
                // 랜덤한 기술 스택 2-4개 선택
                int stackCount = random.nextInt(3) + 2;
                Set<String> selectedStacks = new HashSet<>();
                while (selectedStacks.size() < stackCount) {
                    selectedStacks.add(techStacks[random.nextInt(techStacks.length)]);
                }
                String stackString = String.join(",", selectedStacks);

                mentoringClasses.add(MentoringClass.builder()
                        .mentor(mentor)
                        .title(faker.lorem().sentence(3))
                        .content(faker.lorem().paragraph(3))
                        .price((random.nextInt(90) + 10) * 1000) // 10,000원 ~ 100,000원
                        .stack(stackString)
                        .favoriteCount(random.nextInt(100))
                        .build());
            }
        }
        return mentoringClasses;
    }

    private List<Schedule> createSchedules(List<MentoringClass> mentoringClasses) {
        List<Schedule> schedules = new ArrayList<>();
        Random random = new Random();
        DayOfWeek[] days = DayOfWeek.values();
        LocalTime[] timeSlots = {
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0),
                LocalTime.of(19, 0), LocalTime.of(20, 0), LocalTime.of(21, 0)
        };

        for (MentoringClass mentoringClass : mentoringClasses) {
            // 각 클래스당 3-5개의 요일을 랜덤하게 선택
            int dayCount = random.nextInt(3) + 3;
            Set<DayOfWeek> selectedDays = new HashSet<>();
            while (selectedDays.size() < dayCount) {
                selectedDays.add(days[random.nextInt(days.length)]);
            }

            // 선택된 요일마다 시간대 생성
            for (DayOfWeek day : selectedDays) {
                // 각 요일당 1-2개의 시간대 선택
                int timeSlotCount = random.nextInt(2) + 1;
                Set<LocalTime> selectedTimes = new HashSet<>();
                while (selectedTimes.size() < timeSlotCount) {
                    selectedTimes.add(timeSlots[random.nextInt(timeSlots.length)]);
                }

                // 선택된 시간대로 스케줄 생성
                for (LocalTime time : selectedTimes) {
                    schedules.add(Schedule.builder()
                            .mentoringClassId(mentoringClass.getId())
                            .dayOfWeek(day)
                            .time(time.toString())
                            .build());
                }
            }
        }
        return schedules;
    }

    private List<Favorite> createFavorites(List<Member> members, List<MentoringClass> mentoringClasses) {
        List<Favorite> favorites = new ArrayList<>();
        Random random = new Random();

        // 각 멘토링 클래스마다 즐겨찾기 생성
        for (MentoringClass mentoringClass : mentoringClasses) {
            int favoriteCount = mentoringClass.getFavoriteCount(); // 멘토링 클래스의 즐겨찾기 개수만큼 생성
            Set<Long> selectedMemberIds = new HashSet<>();

            while (selectedMemberIds.size() < favoriteCount) {
                Member member = members.get(random.nextInt(members.size()));
                if (selectedMemberIds.add(member.getId())) {
                    favorites.add(Favorite.builder()
                            .memberId(member.getId())
                            .mentoringClassId(mentoringClass.getId())
                            .build());
                }
            }
        }
        return favorites;
    }
}