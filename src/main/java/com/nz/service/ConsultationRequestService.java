package com.nz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nz.data.ConsultationRequestDTO;
import com.nz.entity.ConsultationRequestEntity;
import com.nz.entity.UserEntity;
import com.nz.repository.ConsultationRequestRepository;
import com.nz.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationRequestService {

    private final ConsultationRequestRepository consultationRequestRepository;
    private final AlarmService alarmService;
    private final UserRepository userRepository;

    public void createConsultationRequest(ConsultationRequestDTO dto, String username) {
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

    public Page<ConsultationRequestDTO> getAllConsultationRequests(PageRequest pageRequest) {
        return consultationRequestRepository.findAllOrderByStatusAndVisitDate(pageRequest).map(this::convertToDTO);
    }

    private ConsultationRequestDTO convertToDTO(ConsultationRequestEntity entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String visitDateString = entity.getVisitDate().toLocalDateTime().toLocalDate().format(formatter);

        return ConsultationRequestDTO.builder()
                .requestId(entity.getRequestId())
                .propertyId(entity.getPropertyId())
                .memberId(entity.getMemberId())
                .visitDate(visitDateString)
                .visitTime(entity.getVisitTime())
                .requestDate(entity.getRequestDate())
                .status(entity.getStatus())
                .build();
    }
    
    @Transactional
    public void updateStatus(List<Long> ids, String status) {
        ids.forEach(id -> {
            ConsultationRequestEntity request = consultationRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));
            request.setStatus(status);
            consultationRequestRepository.save(request);
            
            UserEntity user = userRepository.findByUsername(request.getMemberId())
            		.orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + request.getMemberId()));
            
            String title = "방문 상담 상태 변경 알림";
            String message = "고객님의 방문 상담 신청이 '" + status + "' 상태로 변경되었습니다."; 
            alarmService.createNotificationForUser(user, title, message);
        });
    }
}








