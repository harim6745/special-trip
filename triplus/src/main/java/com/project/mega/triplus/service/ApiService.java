package com.project.mega.triplus.service;

import com.project.mega.triplus.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ApiService {
    private final String ATTRACTION = "12";
    private final String ACTIVITY = "15";
    private final String HOTEL = "32";
    private final String SHOP = "38";
    private final String FOOD = "39";
    private final String KEY;
    private final String APP_NAME = "TRIPLus";
    private final String Y = "Y";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public ApiService(@Value("${my.api.key}") String KEY){
        this.KEY = KEY;
    }

    public boolean loadPlaces() {
        try {
            loadPlaceWithContentType(ATTRACTION);
            loadPlaceWithContentType(ACTIVITY);
            loadPlaceWithContentType(HOTEL);
            loadPlaceWithContentType(SHOP);
            loadPlaceWithContentType(FOOD);
        } catch (IOException | JAXBException e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private void loadPlaceWithContentType(String contentType) throws IOException, JAXBException {
        List<XMLResponseItem> items;
        String xmlString;
        XMLResponse response;

        int pageIdx = 0;
        Place place = null;

        do {
            xmlString = getAreaBasedListXML(contentType, null, ++pageIdx);
            response = getXMLResponse(xmlString);
            items = response.getBody().getItemContainer().getItems();

            for (XMLResponseItem item : items) {
                switch (contentType) {
                    case ACTIVITY:
                        place = new Activity();
                        break;
                    case ATTRACTION:
                        place = new Attraction();
                        break;
                    case HOTEL:
                        place = new Accomm();
                        break;
                    case FOOD:
                        place = new Food();
                        break;
                    case SHOP:
                        place = new Shop();
                        break;
                }

                if (place != null) {
                    place.setContentType(contentType);
                    place.setName(item.getPlaceName());
                    place.setContentId(item.getContentId());
                    place.setAddr(item.getAddr1());
                    place.setMapX(item.getMapX());
                    place.setMapY(item.getMapY());
                    place.setAreaCode(item.getAreacode());
                    place.setAddr1(item.getAddr1());
                    if(item.getImageUrl() == null){
                        String DEFAULT_IMAGE = "https://images.unsplash.com/photo-1580907114587-148483e7bd5f?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80";
                        item.setImageUrl(DEFAULT_IMAGE);
                    }
                    place.setThumbnailUrl(item.getImageUrl());
                    place.setTel(item.getTel());

                    em.persist(place);
                    em.flush();
                    em.clear();
                }
            }

        } while (Integer.parseInt(response.getBody().getNumOfRows()) * Integer.parseInt(response.getBody().getPageNo()) <= Integer.parseInt(response.getBody().getTotalCount()));
    }

    public String getSearchKeywordXML(String keyword) throws IOException {
        StringBuilder urlBuilder = getStringBuilder("searchKeyword");

        addParam(urlBuilder, "keyword", keyword);

        return getXMLString(urlBuilder);
    }

    public String getDetailCommonXML(String contentId) throws IOException{
        StringBuilder urlBuilder = getStringBuilder("detailCommon");

        addParam(urlBuilder, "contentId", contentId);
        addParam(urlBuilder, "defaultYN", Y);
        addParam(urlBuilder, "firstImageYN", Y);
        addParam(urlBuilder, "addrinfoYN", Y);
        addParam(urlBuilder, "areacodeYN", Y);
        addParam(urlBuilder, "mapinfoYN", Y);
        addParam(urlBuilder, "overviewYN", Y);

        return getXMLString(urlBuilder);
    }


    private String getDetailImageXML(String contentId) throws IOException {
        StringBuilder urlBuilder = getStringBuilder("detailImage");

        addParam(urlBuilder, "contentId", contentId);
        addParam(urlBuilder, "imageYN", Y);
        addParam(urlBuilder, "subImageYN", Y);

        return getXMLString(urlBuilder);
    }

    private String getLocationBasedListXML(String mapX, String mapY, String radius, String contentTypeId) throws IOException{
        StringBuilder urlBuilder = getStringBuilder("locationBasedList");

        addParam(urlBuilder, "mapX", mapX);
        addParam(urlBuilder, "mapY", mapY);
        addParam(urlBuilder, "radius", radius);
        addParam(urlBuilder, "listYN", Y);
        addParam(urlBuilder, "contentTypeId", contentTypeId);

        return getXMLString(urlBuilder);
    }

    public String getAreaBasedListXML(String contentTypeId, String areaCode, int pageNo) throws IOException{
        StringBuilder urlBuilder = getStringBuilder("areaBasedList");

        if(null != contentTypeId) {
            addParam(urlBuilder, "contentTypeId", contentTypeId);
        }

        if(null != areaCode){
            addParam(urlBuilder, "areaCode", areaCode);
        }

        addParam(urlBuilder, "pageNo", String.valueOf(pageNo));

        return getXMLString(urlBuilder);
    }

    private StringBuilder getStringBuilder(String service) {
        String API_URL = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/";
        StringBuilder urlBuilder= new StringBuilder(API_URL + service);
        urlBuilder.append("?").append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8)).append("=").append(KEY);

        addParam(urlBuilder, "MobileOS", "ETC");
        addParam(urlBuilder, "MobileApp", APP_NAME);
        String PAGE_SIZE = "10000";
        addParam(urlBuilder, "numOfRows", PAGE_SIZE);

        return urlBuilder;
    }

    private String getXMLString(StringBuilder urlBuilder) throws IOException {
        URL url;
        BufferedReader rd;
        StringBuilder sb;
        HttpURLConnection conn;
        String line;

        url = new URL(urlBuilder.toString());
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        sb = new StringBuilder();

        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        return sb.toString();
    }

    private void addParam(StringBuilder sb, String title, String value){
        sb.append("&").append(URLEncoder.encode(title, StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    public XMLResponse getXMLResponse(String xmlString) throws JAXBException{
        JAXBContext jaxbContext = JAXBContext.newInstance(XMLResponse.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (XMLResponse) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
    }

    public XMLResponseItem getItemByContentId(String contentId) {
        String xmlString;
        XMLResponse response;
        XMLResponseItem item = null;
        List<XMLResponseItem> images;

        try{
            xmlString = getDetailCommonXML(contentId);
            response = getXMLResponse(xmlString);
            item = response.getBody().getItemContainer().getItems().get(0);

            xmlString = getDetailImageXML(contentId);
            response = getXMLResponse(xmlString);
            images = response.getBody().getItemContainer().getItems();

            for(XMLResponseItem image : images){
                item.getImageUrls().add(image.getOriginImageUrl());
            }

        } catch (IOException | JAXBException e){
            log.error(e.getMessage());
        }

        return item;
    }

    public List<XMLResponseItem> getItemByMapXAndMapY(String mapX, String mapY, String radius, String contentTypeId) {
        String xmlString;
        XMLResponse response;
        List<XMLResponseItem> itemList;

        try{
            xmlString = getLocationBasedListXML(mapX, mapY, radius, contentTypeId);
            response = getXMLResponse(xmlString);

            itemList = new ArrayList<>(response.getBody().getItemContainer().getItems());

        } catch (IOException | JAXBException e){
            log.error(e.getMessage());
            itemList = null;
        }

        return itemList;
    }

    public List<XMLResponseItem> getKeywordResultList(String keyword) {
        String xmlString;
        XMLResponse response;
        List<XMLResponseItem> itemList;

        try{
            xmlString = getSearchKeywordXML(keyword);
            response = getXMLResponse(xmlString);
            itemList = response.getBody().getItemContainer().getItems();

        } catch (IOException | JAXBException e){
            itemList = null;
            log.error(e.getMessage());
        }

        return itemList;
    }

}