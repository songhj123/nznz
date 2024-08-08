package com.nz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nz.entity.ContractEntity;
import com.nz.entity.UserEntity;

public interface ContractRepository extends JpaRepository<ContractEntity, Long>{
	 Optional<ContractEntity> findByPropertyPropertyId(Long propertyId);
	 Page<ContractEntity> findAll(Pageable pageable);
	 List<ContractEntity> findByLandlordOrTenant(UserEntity landlord, UserEntity tenant);
}
