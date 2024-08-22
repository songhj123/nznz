package com.nz.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nz.data.ConsultationRequestDTO;
import com.nz.service.ConsultationRequestService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ReservationController {
	
	private final ConsultationRequestService consultationRequestService;
	
    @GetMapping("/reservations/{propertyId}")
    public String viewReservations(@PathVariable("propertyId") Long propertyId, Model model) {
        List<ConsultationRequestDTO> reservations = consultationRequestService.getReservationsByPropertyId(propertyId);
        model.addAttribute("reservations", reservations);
        return "user/myReservation_list";  // 예약 신청 목록을 보여주는 템플릿
    }
}
