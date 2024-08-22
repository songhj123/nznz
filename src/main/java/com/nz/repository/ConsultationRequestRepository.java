package com.nz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nz.entity.ConsultationRequestEntity;

@Repository
public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequestEntity, Long> {
	 @Query("SELECT c FROM ConsultationRequestEntity c ORDER BY " +
		       "CASE WHEN c.status = '신청중' AND c.visitDate >= CURRENT_DATE THEN 1 " + // 신청중이고 방문 날짜가 오늘 이후인 경우
		       "WHEN c.status = '신청중' AND c.visitDate < CURRENT_DATE THEN 2 " + // 신청중이고 방문 날짜가 지난 경우
		       "ELSE 3 END, " + // 신청중이 아닌 경우
		       "CASE WHEN c.status = '신청중' AND c.visitDate >= CURRENT_DATE THEN c.visitDate END ASC, " + // 신청중이고 방문 날짜가 오늘 이후인 경우는 날짜 오름차순
		       "CASE WHEN c.status <> '신청중' THEN c.requestDate END DESC") // 신청중이 아닌 경우는 신청 날짜 내림차순
	 Page<ConsultationRequestEntity> findAllOrderByStatusAndVisitDate(Pageable pageable);
	 
	 List<ConsultationRequestEntity> findByPropertyId(Long propertyId);
}