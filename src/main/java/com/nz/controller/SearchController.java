package com.nz.controller;

import com.nz.data.PropertyDTO;
import com.nz.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<PropertyDTO> properties = searchService.searchProperties(query);
        model.addAttribute("properties", properties);
        return "property/properties";
    }
}
