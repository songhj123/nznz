package com.nz.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nz.data.ContractDTO;
import com.nz.data.PropertyDTO;
import com.nz.data.PropertyImageDTO;
import com.nz.data.PropertyOptionDTO;
import com.nz.data.UserDTO;
import com.nz.entity.ContractEntity;
import com.nz.entity.PropertyEntity;
import com.nz.entity.PropertyImageEntity;
import com.nz.entity.PropertyOptionEntity;
import com.nz.entity.UserEntity;
import com.nz.repository.ContractRepository;

@Service
public class ContractService {
	
	@Autowired
	private ContractRepository contractRepository;

	public ContractDTO getContractByPropertyId(Long propertyId) {
		Optional<ContractEntity> op = contractRepository.findByPropertyPropertyId(propertyId);
		if(op.isPresent()) {
			ContractEntity ce = op.get();
			
			return convertToDTO(ce); 
		}else {
			throw new RuntimeException("contract not found");
		}											
	}
	
	public Page<ContractDTO> getAllContracts(Pageable pageable){
		return contractRepository.findAll(pageable)
				.map(this :: convertToDTO);

	}
	
	private ContractDTO convertToDTO(ContractEntity contractEntity) {
		PropertyDTO propertyDTO = convertToPropertyDTO(contractEntity.getProperty()); 
		UserDTO landlordDTO = convertToUserDTO(contractEntity.getLandlord());
		UserDTO tenantDTO = convertToUserDTO(contractEntity.getTenant());
				
		return ContractDTO.builder()
				.contractId(contractEntity.getContractId())
				.propertyId(propertyDTO)
				.landlordId(landlordDTO)
				.tenantId(tenantDTO)
				.stage(contractEntity.getStage())
				.contractDate(contractEntity.getContractDate())
				.expirationDate(contractEntity.getExpirationDate())
				.build();
	}
	
	private PropertyDTO convertToPropertyDTO(PropertyEntity propertyEntity) {
		
		List<PropertyImageDTO> imageDTOList = propertyEntity.getPropertyImageList().stream()
				.map(this :: convertToImageDTO)
				.collect(Collectors.toList());
		List<PropertyOptionDTO> optionDTOlist = propertyEntity.getPropertyOptions().stream()
				.map(this :: convertToOptionDTO)
				.collect(Collectors.toList());
		
		return PropertyDTO.builder()
				.propertyId(propertyEntity.getPropertyId())
				.propertyNum(propertyEntity.getPropertyNum())
				.memberId(propertyEntity.getMemberId())
				.propertyType(propertyEntity.getPropertyType())
				.propertyAddress(propertyEntity.getPropertyAddress())
				.buildingName(propertyEntity.getBuildingName())
				.sizePyeong(propertyEntity.getSizePyeong())
				.roomInfo(propertyEntity.getRoomInfo())
				.deposit(propertyEntity.getDeposit())
				.monthlyRent(propertyEntity.getMonthlyRent())
				.maintenanceFee(propertyEntity.getMaintenanceFee())
				.availableDate(propertyEntity.getAvailableDate())
				.floor(propertyEntity.getFloor())
				.shortDescription(propertyEntity.getShortDescription())
				.longDescription(propertyEntity.getLongDescription())
				.registrationDate(propertyEntity.getRegistrationDate())
				.status(propertyEntity.getStatus())
				.processingStatus(propertyEntity.getProcessingStatus())
				.latitude(propertyEntity.getLatitude())
				.longitude(propertyEntity.getLongitude())
				.propertyImageList(imageDTOList)
				.propertyOption(optionDTOlist)
				.build();
	}
	
	private PropertyImageDTO convertToImageDTO(PropertyImageEntity propertyImageEntity) {		
		return PropertyImageDTO.builder()
				.imageId(propertyImageEntity.getImageId())
				.imageOriginalName(propertyImageEntity.getImageOriginalName())
				.imageStoredName(propertyImageEntity.getImageStoredName())
				.build();
	}
	
	private PropertyOptionDTO convertToOptionDTO(PropertyOptionEntity propertyOptionEntity) {
		return PropertyOptionDTO.builder()
				.heatingSystem(propertyOptionEntity.getHeatingSystem())
				.coolingSystem(propertyOptionEntity.getCoolingSystem())
				.livingFacilities(propertyOptionEntity.getLivingFacilities())
				.securityFacilities(propertyOptionEntity.getSecurityFacilities())
				.otherFacilities(propertyOptionEntity.getOtherFacilities())
				.parking(propertyOptionEntity.getParking())
				.elevator(propertyOptionEntity.getElevator())
				.propertyFeatures(propertyOptionEntity.getPropertyFeatures())
				.build();
	}
	
	private UserDTO convertToUserDTO(UserEntity userEntity) {
		return UserDTO.builder()
				.memberId(userEntity.getMemberID())
				.username(userEntity.getUsername())
				.name(userEntity.getName())
				.email(userEntity.getEmail())
				.phoneNumber(userEntity.getPhoneNumber())
				.accountNumber(userEntity.getAccountNumber())
				.verified(userEntity.getVerified())
				.createDate(Timestamp.valueOf(userEntity.getCreateDate()))
				.role(userEntity.getRole())
				.build();
	}
}






