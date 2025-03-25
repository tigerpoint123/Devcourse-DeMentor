package com.dementor.domain.mentoringclass.controller;

import com.dementor.domain.mentoringclass.service.MentoringClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class MentoringClassController {
    private final MentoringClassService mentoringClassService;

    @PostMapping
    public String createClass() {


        return "Hello World";
    }

    @GetMapping
    public String hello() {
        return "Hello World";
    }


}
