package com.nz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nz.entity.AlarmReferenceEntity;

public interface AlarmReferenceRepository extends JpaRepository<AlarmReferenceEntity, Long> {
}
