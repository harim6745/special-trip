package com.project.mega.triplus.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "header")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter @ToString
public class XMLResponseHeader {
    private String resultCode;
    private String resultMsg;
}
