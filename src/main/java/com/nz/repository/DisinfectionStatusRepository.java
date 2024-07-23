package com.nz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nz.entity.DisinfectionStatusEntity;

public interface DisinfectionStatusRepository extends JpaRepository<DisinfectionStatusEntity, Long> {
}