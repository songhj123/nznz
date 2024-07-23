package com.nz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nz.data.ConsultationRequestDTO;
import com.nz.entity.ConsultationRequestEntity;
import com.nz.repository.ConsultationRequestRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ConsultationRequestService {

    @Autowired
    private ConsultationRequestRepository consultationRequestRepository;

    public void createConsultationRequest(ConsultationRequestDTO dto,String username) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Timestamp visitDateTimestamp = Timestamp.valueOf(LocalDate.parse(dto.getVisitDate(), formatter).atStartOfDay());
        
        ConsultationRequestEntity consultationRequest = ConsultationRequestEntity.builder()
                .propertyId(dto.getPropertyId())
                .memberId(username)
                .visitDate(visitDateTimestamp)
                .visitTime(dto.getVisitTime())
                .requestDate(new Timestamp(System.currentTimeMillis()))
                .status("신청중")
                .build();

        consultationRequestRepository.save(consultationRequest);
    }
}
