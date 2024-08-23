package com.nz.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.DisinfectionStatusDTO;
import com.nz.service.DisinfectionStatusService;

@Controller
@RequestMapping("/admin/*")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class DisinfectionStatusController {
    @Autowired
    private DisinfectionStatusService disinfectionStatusService;

    @GetMapping("disinfectionStatus")
    public String getDisinfectionStatus(Model model,
                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<DisinfectionStatusDTO> disinfectionStatusPage = disinfectionStatusService.getAllDisinfectionStatusDTOs(page, size);
        model.addAttribute("disinfectionStatusPage", disinfectionStatusPage);
        return "admin/disinfectionStatus"; // disinfectionStatus.html 페이지로 이동
    }

    @PostMapping("/disinfectionStatus")
    public String setDisinfectionStatus(@ModelAttribute DisinfectionStatusDTO disinfectionStatusDTO, Principal principal) {
        disinfectionStatusDTO.setUsername(principal.getName());
        disinfectionStatusService.saveDisinfectionStatus(disinfectionStatusDTO);
        return "redirect:/admin/disinfectionStatus";
    }
}
