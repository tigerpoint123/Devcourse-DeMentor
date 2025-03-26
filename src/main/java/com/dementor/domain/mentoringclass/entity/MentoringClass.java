package com.dementor.domain.mentoringclass.entity;

import com.dementor.domain.mentor.entity.MentorEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mentoring_class")
@Getter
@Setter
public class MentoringClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;

    private String stack;

    private String content;

    private int price;

    // TODO : Mentor 엔티티 생성되면 연결
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private MentorEntity mentor;
}
