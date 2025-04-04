package com.dementor.domain.mentor.service;

import com.dementor.domain.mentor.repository.MentorRepoFromHo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorServiceFromHo {
    private final MentorRepoFromHo mentorRepoFromHo;


}
