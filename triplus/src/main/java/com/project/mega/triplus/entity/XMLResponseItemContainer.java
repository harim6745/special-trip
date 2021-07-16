package com.project.mega.triplus.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "items")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter @ToString
public class XMLResponseItemContainer {
    @XmlElement(name = "item")
    private List<XMLResponseItem> items = new ArrayList<>();
}
