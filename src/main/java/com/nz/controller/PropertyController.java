package com.nz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nz.data.PropertyDTO;
import com.nz.service.PropertyService;

@Controller
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/properties")
    public String getProperties(Model model) {
        List<PropertyDTO> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "properties";
    }

    @GetMapping("/properties/within")
    @ResponseBody
    public ResponseEntity<List<PropertyDTO>> getPropertiesWithin(
            @RequestParam("southWestLat") double southWestLat,
            @RequestParam("southWestLng") double southWestLng,
            @RequestParam("northEastLat") double northEastLat,
            @RequestParam("northEastLng") double northEastLng) {
        List<PropertyDTO> properties = propertyService.getPropertiesWithin(southWestLat, southWestLng, northEastLat, northEastLng);
        for (PropertyDTO property : properties) {
            System.out.println("==========================================");
            System.out.println("Property ID: " + property.getPropertyId());
            System.out.println("Property Images: " + property.getPropertyImageList());
        }
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }
}
