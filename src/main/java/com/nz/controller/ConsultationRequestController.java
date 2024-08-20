package com.nz.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    	String redir = "redirect:/property/" + consultationRequestDTO.getPropertyId().toString();
    	try {
            consultationRequestService.createConsultationRequest(consultationRequestDTO, principal.getName());
            redirectAttributes.addFlashAttribute("message", "방문 상담 신청이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redir= "redirect:/consultationRequest/" + consultationRequestDTO.getPropertyId().toString();
        }
        return redir; // 신청 완료 후 목록 페이지로 리디렉션
    }

    @GetMapping("/consultationRequest/list")
    public String consultationRequestList(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size, Model model) {
        Page<ConsultationRequestDTO> consultationRequestPage = consultationRequestService.getAllConsultationRequests(PageRequest.of(page, size));
        model.addAttribute("consultationRequestPage", consultationRequestPage);
        return "admin/consultationRequestList";
    }
    
    @PostMapping("/consultationRequest/updateStatus")
    public String updateStatus(@RequestParam("selectedRequests") List<Long> ids, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        try {
            consultationRequestService.updateStatus(ids, status);
            redirectAttributes.addFlashAttribute("message", "상태가 성공적으로 업데이트되었습니다.");
            return "redirect:/consultationRequest/list"; // 원하는 페이지로 리디렉션
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상태 업데이트 중 오류가 발생했습니다.");
            return "redirect:/consultationRequest/list"; // 에러 발생 시 리디렉션할 페이지
        }
    }
}
