package com.nz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nz.entity.AlarmEntity;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    List<AlarmEntity> findByAlarmId(Long alarmId);
    Page<AlarmEntity> findAll(Pageable pageable);
}
	