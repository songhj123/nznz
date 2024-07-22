package com.nz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nz.data.PropertyDTO;
import com.nz.service.PropertyService;

@Controller
public class ConsultationRequestController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/consultationRequest/{propertyId}")
    public String consultationRequest(@PathVariable("propertyId") Long propertyId, Model model) {
        PropertyDTO property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            // 해당 매물이 존재하지 않을 경우 예외 처리 또는 에러 페이지로 이동
            return "error/404";
        }
        model.addAttribute("property", property);
        return "consultationRequest"; // consultationRequest.html 페이지로 이동
    }
}