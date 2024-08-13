package com.nz.controller;

import com.nz.service.VWorldApiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VWorldApiController {
    
    @Autowired
    private VWorldApiClientService vWorldApiClientService;
    
    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam("query") String query) {
        return vWorldApiClientService.getSuggestions(query);
    }
    
    @GetMapping("/coordinates")
    public double[] getCoordinates(@RequestParam("address") String address) {
        return vWorldApiClientService.getCoordinates(address);
    }
}
