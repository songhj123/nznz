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
import org.springframework.transaction.annotation.Transactional;

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
import com.nz.repository.PropertyRepository;
import com.nz.repository.UserRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class ContractService {
	
	private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    
    
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
	
	
	
	
    public List<ContractDTO> getAllContracts() {
        List<ContractEntity> contractEntities = contractRepository.findAll();
        return contractEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContractDTO> getContractsByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
        List<ContractEntity> contractEntities = contractRepository.findByLandlordOrTenant(user, user);
        return contractEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContractDTO> getContractsByLandlordUsername(String username) {
        UserEntity landlord = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
        List<ContractEntity> contractEntities = contractRepository.findByLandlordOrTenant(landlord, null);
        return contractEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContractDTO> getContractsByTenantUsername(String username) {
        UserEntity tenant = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
        List<ContractEntity> contractEntities = contractRepository.findByLandlordOrTenant(null, tenant);
        return contractEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ContractDTO getContractById(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다."));
        return convertToDTO(contractEntity);
    }

    public void acceptContract(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다."));
        contractEntity.setStage("계약진행수락");
        contractRepository.save(contractEntity);
    }

    public void applyAutomaticTransfer(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다."));
        contractEntity.setStage("자동이체신청");
        contractRepository.save(contractEntity);
    }

    public void advanceToNextStage(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다."));
        switch (contractEntity.getStage()) {
            case "방문상담완료":
                contractEntity.setStage("계약진행수락");
                break;
            case "계약진행수락":
                contractEntity.setStage("각종서류확인");
                break;
            case "각종서류확인":
                contractEntity.setStage("자동이체신청");
                break;
            case "자동이체신청":
                contractEntity.setStage("계약완료");
                break;
            default:
                throw new IllegalStateException("알 수 없는 단계: " + contractEntity.getStage());
        }
        contractRepository.save(contractEntity);
    }

    public void verifyDocuments(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다."));
        contractEntity.setStage("각종서류확인");
        contractRepository.save(contractEntity);
    }

    public void completeContract(Long contractId) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다."));
        contractEntity.setStage("계약완료");
        contractRepository.save(contractEntity);
    }
    
    public void updateContractStatus(Long contractId, String status) {
        ContractEntity contractEntity = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid contract ID"));
        contractEntity.setStage(status);
        contractRepository.save(contractEntity);
    }
    
    
    @Transactional
    public Long createContract(ContractDTO contractDTO) {
        // 계약 생성 전에 검증 로직 추가
        validateContractCreation(contractDTO);

        // PropertyEntity에서 임대인 정보 가져오기 (memberId를 통해)
        PropertyEntity property = propertyRepository.findById(contractDTO.getPropertyId().getPropertyId())
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다: " + contractDTO.getPropertyId().getPropertyId()));
        
        // 임대인 정보가 PropertyEntity의 memberId와 연결되어 있다고 가정
        UserEntity landlord = userRepository.findById(property.getMemberId())
                .orElseThrow(() -> new RuntimeException("임대인을 찾을 수 없습니다: " + property.getMemberId()));
        
        // 임차인 정보 가져오기
        UserEntity tenant = userRepository.findById(contractDTO.getTenantId().getMemberId())
                .orElseThrow(() -> new RuntimeException("임차인을 찾을 수 없습니다: " + contractDTO.getTenantId().getMemberId()));

        // ContractEntity 생성 및 저장
        ContractEntity contract = ContractEntity.builder()
                .property(property)
                .landlord(landlord)
                .tenant(tenant)
                .contractDate(contractDTO.getContractDate())
                .expirationDate(contractDTO.getExpirationDate())
                .stage("방문상담완료")
                .build();

        contractRepository.save(contract);
        return contract.getContractId();
    }


    private void validateContractCreation(ContractDTO contractDTO) {
        if (contractDTO.getContractDate() == null) {
            throw new IllegalArgumentException("계약일이 설정되지 않았습니다.");
        }
        if (contractDTO.getExpirationDate() == null) {
            throw new IllegalArgumentException("만료일이 설정되지 않았습니다.");
        }
        if (contractDTO.getTenantId() == null) {
            throw new IllegalArgumentException("임차인 정보가 누락되었습니다.");
        }
        if (contractDTO.getPropertyId() == null) {
            throw new IllegalArgumentException("매물 정보가 누락되었습니다.");
        }
    }
    
}






