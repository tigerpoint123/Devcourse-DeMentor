package com.dementor.domain.mentor.controller;

import com.dementor.global.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MentorControllerFromHo {

    @GetMapping("/api/mentor/class/{menber_id}")
    public ApiResponse<?> getMentorClassFromMentor() {
        

        return null;
    }
}
