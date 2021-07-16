package com.project.mega.triplus.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class PlanForm {
    private String plan;
    private Long planId;
    private List<List<Map<String, String>>> dayList;
}
