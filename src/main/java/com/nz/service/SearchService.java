package com.nz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nz.data.PropertyDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private static final String API_KEY = "F320E4DD-72C4-3869-9343-68E55DB54345";
    private static final String SEARCH_URL = "https://api.vworld.kr/req/search?service=search&request=search&version=2.0&crs=EPSG:4326&size=10&page=1&query={query}&type=address&category=road&key=" + API_KEY;

    public List<PropertyDTO> searchProperties(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(SEARCH_URL, String.class, query);

        List<PropertyDTO> properties = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("response").path("result").path("items");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    PropertyDTO property = new PropertyDTO();
                    property.setPropertyAddress(item.path("address").asText());
                    property.setLatitude(item.path("point").path("y").asDouble());
                    property.setLongitude(item.path("point").path("x").asDouble());
                    properties.add(property);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
