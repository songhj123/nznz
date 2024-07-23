package com.nz.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nz.data.ConsultationRequestDTO;
import com.nz.data.PropertyDTO;
import com.nz.service.ConsultationRequestService;
import com.nz.service.PropertyService;

@Controller
public class ConsultationRequestController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private ConsultationRequestService consultationRequestService;

    @GetMapping("/consultationRequest/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public String consultationRequest(@PathVariable("propertyId") Long propertyId, Model model) {
        PropertyDTO property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            // 해당 매물이 존재하지 않을 경우 예외 처리 또는 에러 페이지로 이동
            return "error/404";
        }
        model.addAttribute("property", property);
        return "consultation/consultationRequest"; // 변경된 경로로 수정
    }
    
    @PostMapping("/consultationRequest")
    @PreAuthorize("isAuthenticated()")
    public String createConsultationRequest(@ModelAttribute ConsultationRequestDTO consultationRequestDTO, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            consultationRequestService.createConsultationRequest(consultationRequestDTO,principal.getName());
            redirectAttributes.addFlashAttribute("message", "방문 상담 신청이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/consultationRequest/" + consultationRequestDTO.getPropertyId();
        }
        return "redirect:/property/"+ consultationRequestDTO.getPropertyId(); // 신청 완료 후 목록 페이지로 리디렉션
    }

}
