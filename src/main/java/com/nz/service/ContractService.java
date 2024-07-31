package com.nz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nz.data.ContractDTO;
import com.nz.entity.ContractEntity;
import com.nz.repository.ContractRepository;

@Service
public class ContractService {
	
	@Autowired
	private ContractRepository contractRepository;

	public ContractDTO getContractByPropertyId(Long propertyId) {
		Optional<ContractEntity> op = contractRepository.findByPropertyId(propertyId);
		if(op.isPresent()) {
			ContractEntity ce = op.get();
			ContractDTO contractDTO = ContractDTO.builder()
					.contractId(ce.getContractId())
					.propertyId(ce.getPropertyId())
					.landlordId(ce.getLandlordId())
					.tenantId(ce.getTenantId())
					.stage(ce.getStage())
					.contractDate(ce.getContractDate())
					.expirationDate(ce.getExpirationDate())
					.build();
			return contractDTO; 
		}else {
			throw new RuntimeException("contract not found");
		}											
	}
}
