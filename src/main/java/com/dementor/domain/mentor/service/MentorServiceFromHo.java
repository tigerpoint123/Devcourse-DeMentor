package com.dementor.domain.mentor.service;

import com.dementor.domain.mentor.dto.response.MyMentoringResponse;
import com.dementor.domain.mentor.repository.MentorRepoFromHo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorServiceFromHo {
    private final MentorRepoFromHo mentorRepoFromHo;

    public List<MyMentoringResponse> getMentorClassFromMentor(Long menberId) {
        List<MyMentoringResponse> response = mentorRepoFromHo.findByMemerId(menberId);
        return response;
    }
}
