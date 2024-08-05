package com.nz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlarmController {

	@GetMapping("/admin/alarmPush")
	public String alarmPush() {
		return "admin/adminAlarm";
	}
}
