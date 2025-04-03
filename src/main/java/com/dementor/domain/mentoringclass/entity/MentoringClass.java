package com.dementor.domain.mentoringclass.entity;

import java.util.ArrayList;
import java.util.List;

import com.dementor.domain.mentor.entity.Mentor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mentoring_class")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentoringClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;

    private String stack;

    private String content;

    private int price;

    @OneToMany(mappedBy = "mentoringClass", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.content = description;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

}
