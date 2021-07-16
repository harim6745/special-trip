package com.project.mega.triplus.service;

import com.project.mega.triplus.entity.*;
import com.project.mega.triplus.form.PlanForm;
import com.project.mega.triplus.repository.PlaceRepository;
import com.project.mega.triplus.repository.PlanRepository;
import com.project.mega.triplus.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Service
@Getter
@RequiredArgsConstructor
@Transactional
public class PlanService {
    @PersistenceContext
    private EntityManager em;

    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;


    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<Plan> getAllByOrderByLikedDesc(){
        return planRepository.findAllByOrderByLikedDesc();
    }

    public Long savePlan(User user, Plan plan, PlanForm planForm) {
        Day day;

        plan.setUser(user);
        plan.setName(planForm.getPlan());
        plan.setUpdateTime(LocalDateTime.now());

        List<Day> tmpList = new ArrayList<>();

        for(List<Map<String, String>> d : planForm.getDayList()){
            day = new Day();

            for(Map<String, String> p : d){
                String content_id = p.get("content_id");
                Place singleResult = em.createQuery("select p from Place p where p.contentId = :contentId", Place.class).setParameter(
                        "contentId", content_id
                ).getSingleResult();

                day.addPlace(singleResult);
            }

            day.setPlan(plan);
            tmpList.add(day);
        }
        plan.getDays().clear();
        plan.getDays().addAll(tmpList);

        em.persist(plan);

        return plan.getId();
    }


    public Plan getPlanById(Long id) {
        return em.find(Plan.class, id);
    }
}
