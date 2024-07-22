package com.nz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nz.entity.ConsultationRequestEntity;

@Repository
public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequestEntity, Long> {
}