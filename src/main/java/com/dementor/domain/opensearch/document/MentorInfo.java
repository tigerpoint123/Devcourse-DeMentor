package com.dementor.domain.opensearch.document;

import com.dementor.domain.mentor.entity.Mentor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MentorInfo {
    private Long id;
    private String name;
    private JobInfo job;
    private int career;

    public static MentorInfo from(Mentor mentor) {
        MentorInfo mentorInfo = new MentorInfo();
        mentorInfo.setId(mentor.getId());
        mentorInfo.setName(mentor.getName());
        mentorInfo.setJob(JobInfo.from(mentor.getJob()));
        mentorInfo.setCareer(mentor.getCareer());
        return mentorInfo;
    }
} 