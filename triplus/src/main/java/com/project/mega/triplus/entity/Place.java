package com.project.mega.triplus.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String homepage;

    private String addr;

    private String areaCode;

    private String tel;

    private String content;

    private String thumbnailUrl;

    private String addr1;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    private int liked;

    @OneToMany(mappedBy = "place", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Review> reviews = new ArrayList<>();

    private String contentId;

    private String contentType;

    private String mapX;

    private String mapY;
}
