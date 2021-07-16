package com.project.mega.triplus.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter @ToString
public class XMLResponseItem {

    private String code;    // areaCode : 지역 코드

    private String name;    // areaCode : 지역 이름

    private String rnum;    // areaCode : 행 number

    private String addr1;           // 상세 주소

    private String addr2;            // 동

    private String areacode;        // 지역 코드

    @XmlElement(name = "contentid")
    private String contentId;       // 콘텐츠 고유 아이디

    @XmlElement(name = "contenttypeid")
    private String contentTypeId;   // 콘텐츠 타입 아이디()

    @XmlElement(
            name = "firstimage",
            required = true,
            defaultValue = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1351&q=80"

    )
    private String imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1351&q=80";

    @XmlElement(name = "mapx")
    private String mapX;

    @XmlElement(name = "mapy")
    private String mapY;

    @XmlElement(name = "title")
    private String placeName;

    private String overview;

    private String cat1;

    private String cat2;

    private String cat3;

    private String tel;

    private String homepage;

    // for images

    @XmlElement(
            name = "originimgurl",
            required = true,
            defaultValue = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1351&q=80"
    )
    private String originImageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1351&q=80";

    private Set<String> imageUrls = new HashSet<>();

    /*
    @XmlElement(name = "infoname")
    private String infoName;
    @XmlElement(name = "infotext")
    private String infoText;
     */
}