package com.nz.repository;

import com.nz.entity.PropertyOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyOptionRepository extends JpaRepository<PropertyOptionEntity, Long> {
    List<PropertyOptionEntity> findByProperty_PropertyId(Long propertyId);
}
