package com.nz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nz.service.DisinfectionStatusService;

@Controller
@RequestMapping("/admin/*")
public class DisinfectionStatusController {
    @Autowired
    private DisinfectionStatusService disinfectionStatusService;
    
    @GetMapping
    public String cisinfectionStatus() {
    	return "admin/disinfectionStatus";
    }
}
