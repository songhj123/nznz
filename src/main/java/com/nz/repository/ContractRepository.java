package com.nz.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nz.entity.ContractEntity;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>{
	 Optional<ContractEntity> findByPropertyPropertyId(Long propertyId);
	 Page<ContractEntity> findAll(Pageable pageable);
}
