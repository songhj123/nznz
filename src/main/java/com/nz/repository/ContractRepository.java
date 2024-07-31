package com.nz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nz.entity.ContractEntity;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>{
	 Optional<ContractEntity> findByPropertyId(Long propertyId);
}
