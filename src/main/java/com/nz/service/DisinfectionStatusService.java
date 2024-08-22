package com.nz.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nz.data.DisinfectionStatusDTO;
import com.nz.entity.DisinfectionStatusEntity;
import com.nz.repository.DisinfectionStatusRepository;

@Service
public class DisinfectionStatusService {

    @Autowired
    private DisinfectionStatusRepository disinfectionStatusRepository;

    public List<DisinfectionStatusEntity> getAllDisinfectionStatuses() {
        return disinfectionStatusRepository.findAll();
    }

    public Page<DisinfectionStatusDTO> getAllDisinfectionStatusDTOs(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return disinfectionStatusRepository.findAllByOrderByUpdatedAtDesc(pageRequest)
            .map(this::convertToDTO);
    }
    
    public DisinfectionStatusDTO getLatestDisinfectionStatus() {
        DisinfectionStatusEntity entity = disinfectionStatusRepository.findTopByOrderByUpdatedAtDesc();
        return convertToDTO(entity);
    }

    private DisinfectionStatusDTO convertToDTO(DisinfectionStatusEntity entity) {
        return DisinfectionStatusDTO.builder()
            .levelId(entity.getLevelId())
            .disinfectionLevel(entity.getDisinfectionLevel())
            .reasonTitle(entity.getReasonTitle())
            .reasonDetails(entity.getReasonDetails())
            .username(entity.getUsername())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public void saveDisinfectionStatus(DisinfectionStatusDTO dto) {
        DisinfectionStatusEntity entity = DisinfectionStatusEntity.builder()
            .disinfectionLevel(dto.getDisinfectionLevel())
            .reasonTitle(dto.getReasonTitle())
            .reasonDetails(dto.getReasonDetails())
            .username(dto.getUsername())
            .updatedAt(new Timestamp(System.currentTimeMillis()))
            .build();
        disinfectionStatusRepository.save(entity);
    }
    
    
}
