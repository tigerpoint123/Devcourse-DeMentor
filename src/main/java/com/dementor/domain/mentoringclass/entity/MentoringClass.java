package com.dementor.domain.mentoringclass.entity;

import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentoring_class")
//@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentoringClass extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private String title;

    private String stack;

    @Getter
    private String content;

    @Getter
    private int price;

    @OneToMany(mappedBy = "mentoringClass", cascade = CascadeType.ALL)
    @Getter
    private List<Schedule> schedules = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    @Getter
    private Mentor mentor;

    public void updateTitle(String title) {
        this.title = title;
    }

    public String[] getStack() {
        return this.stack.split(",");
    }

    public void updateContent(String description) {
        this.content = description;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public void updateStack(String[] stack) {
        this.stack = String.join(",", stack);
    }

    public void updateSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
