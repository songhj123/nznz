package com.nz.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.nz.data.AlarmDTO;
import com.nz.service.AlarmService;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class AlarmController {

	private final AlarmService ALARMSERVICE;

	
	@GetMapping("/admin/alarmPush")
	public String alarmPush(Model model,Pageable pageable) {
		Page<AlarmDTO> notifications = ALARMSERVICE.getAllNotifications(pageable);
        model.addAttribute("notifications", notifications);
		return "admin/adminAlarm";
	}
	
	@PostMapping("/admin/alarmPushAll")
	public String alarmPushAll(Model model,AlarmDTO alarmDTO,Principal principal) {
		alarmDTO.setUsername(principal.getName());
		ALARMSERVICE.createNotificationForAll(alarmDTO);
		return "redirect:/admin/alarmPush";
	}	
}
	