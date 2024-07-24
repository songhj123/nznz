package com.nz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nz.entity.ConsultationRequestEntity;

@Repository
public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequestEntity, Long> {
	 @Query("SELECT c FROM ConsultationRequestEntity c ORDER BY CASE WHEN c.status = '신청중' THEN 0 ELSE 1 END, c.visitDate ASC")
	 Page<ConsultationRequestEntity> findAllOrderByStatusAndVisitDate(Pageable pageable);
}