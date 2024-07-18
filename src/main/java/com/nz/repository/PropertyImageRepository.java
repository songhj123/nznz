package com.nz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nz.entity.PropertyImageEntity;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImageEntity, Long> {
    List<PropertyImageEntity> findByProperty_PropertyId(Long propertyId);
}
