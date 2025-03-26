package com.dementor.domain.categories.entity;


import com.dementor.domain.mentor.entity.Mentor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    // Mentor 엔티티와의 관계 (일대다)
    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Mentor> mentors = new ArrayList<>();
}

