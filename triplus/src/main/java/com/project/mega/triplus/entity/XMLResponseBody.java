package com.project.mega.triplus.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "body")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter @ToString
public class XMLResponseBody {
    @XmlElement(name = "items")
    private XMLResponseItemContainer itemContainer;
    private String numOfRows;
    private String pageNo;
    private String totalCount;
}
