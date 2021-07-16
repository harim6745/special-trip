package com.project.mega.triplus.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Place> places = new ArrayList<>();

    private String placeImg;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @JsonBackReference
    private Plan plan;

    public void setPlan(Plan plan) {
        this.plan = plan;
        plan.getDays().add(this);
        plan.setDayCounts(plan.getDayCounts()+1);
    }

    public void addPlace(Place place){
        places.add(place);
    }
}