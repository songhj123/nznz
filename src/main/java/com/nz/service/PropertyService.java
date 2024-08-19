package com.nz.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nz.data.PropertyDTO;
import com.nz.data.PropertyImageDTO;
import com.nz.data.PropertyOptionDTO;
import com.nz.entity.PropertyEntity;
import com.nz.entity.PropertyImageEntity;
import com.nz.entity.PropertyOptionEntity;
import com.nz.entity.UserEntity;
import com.nz.repository.PropertyImageRepository;
import com.nz.repository.PropertyOptionRepository;
import com.nz.repository.PropertyRepository;
import com.nz.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyService {

    
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PropertyOptionRepository propertyOptionRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;
    
    public List<PropertyDTO> getAllProperties() {
        List<PropertyEntity> properties = propertyRepository.findAll();
        return properties.stream()
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                })
                .collect(Collectors.toList());
    }
    
    public Page<PropertyDTO> getAllPropertiesPaged(PageRequest pageRequest) {
        return propertyRepository.findAll(pageRequest)
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                });
    }

    public List<PropertyDTO> getPropertiesWithin(double swLat, double swLng, double neLat, double neLng) {
        List<PropertyEntity> properties = propertyRepository.findPropertiesWithin(swLat, swLng, neLat, neLng);
        return properties.stream()
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                })
                .collect(Collectors.toList());
    }

    public PropertyDTO getPropertyById(Long id) {
        PropertyEntity propertyEntity = propertyRepository.findById(id).orElse(null);
        if (propertyEntity == null) {
            return null;
        }
        List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(propertyEntity.getPropertyId());
        PropertyDTO propertyDTO = convertToDTO(propertyEntity, images);
        return propertyDTO;
    }

    private PropertyDTO convertToDTO(PropertyEntity propertyEntity, List<PropertyImageEntity> images) {
        List<PropertyImageDTO> imageDTOList = images.stream()
                .map(this::convertToImageDTO)
                .collect(Collectors.toList());

        List<PropertyOptionDTO> optionDTOList = propertyEntity.getPropertyOptions().stream()
                .map(this::convertToOptionDTO) // convertToOptionDTO 메서드를 사용하여 변환
                .collect(Collectors.toList());
        
        UserEntity user = null;
        if (propertyEntity.getMemberId() != null) {
            user = userRepository.findById(propertyEntity.getMemberId())
                    .orElse(null);  // 사용자가 존재하지 않을 경우 예외 처리를 고려
        }

        // 사용자 정보가 없을 경우 기본값 설정
        String username = (user != null) ? user.getUsername() : "Unknown";
        String name = (user != null) ? user.getName() : "Unknown";
        
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
		        .propertyOption(optionDTOList)
		        .username(username)
		        .name(name)
		        .build();

    }

    private PropertyImageDTO convertToImageDTO(PropertyImageEntity propertyImageEntity) {
        return PropertyImageDTO.builder()
                .imageId(propertyImageEntity.getImageId())
                .imageOriginalName(propertyImageEntity.getImageOriginalName())
                .imageStoredName(propertyImageEntity.getImageStoredName())
                .build();
    }
    
    private PropertyOptionDTO convertToOptionDTO(PropertyOptionEntity optionEntity) {
        return PropertyOptionDTO.builder()
                .heatingSystem(optionEntity.getHeatingSystem())
                .coolingSystem(optionEntity.getCoolingSystem())
                .livingFacilities(optionEntity.getLivingFacilities())
                .securityFacilities(optionEntity.getSecurityFacilities())
                .otherFacilities(optionEntity.getOtherFacilities())
                .parking(optionEntity.getParking())
                .elevator(optionEntity.getElevator())
                .propertyFeatures(optionEntity.getPropertyFeatures())
                .build();
    }
    
    public void createProperty(PropertyDTO propertyDTO) {
    	List<PropertyOptionEntity> options = propertyDTO.getPropertyOption().stream().map(optionDTO -> 
    		PropertyOptionEntity.builder()
    			.heatingSystem(optionDTO.getHeatingSystem())
    			.coolingSystem(optionDTO.getCoolingSystem())
    			.livingFacilities(optionDTO.getLivingFacilities())
    			.securityFacilities(optionDTO.getSecurityFacilities())
    			.otherFacilities(optionDTO.getOtherFacilities())
    			.parking(optionDTO.getParking())
    			.elevator(optionDTO.getElevator())
    			.propertyFeatures(optionDTO.getPropertyFeatures())
    			.build()
    		).collect(Collectors.toList());
    	
    	List<PropertyImageEntity> images = propertyDTO.getPropertyImageList().stream().map(imageDTO ->
    			PropertyImageEntity.builder()
    			.imageOriginalName(imageDTO.getImageOriginalName())
    			.imageStoredName(imageDTO.getImageStoredName())
    			.build()
    		).collect(Collectors.toList());
    	
    	PropertyEntity pe = PropertyEntity.builder()
    			.memberId(propertyDTO.getMemberId())
    			.propertyType(propertyDTO.getPropertyType())
    			.propertyAddress(propertyDTO.getPropertyAddress())
    			.buildingName(propertyDTO.getBuildingName())
    			.sizePyeong(propertyDTO.getSizePyeong())
    			.roomInfo(propertyDTO.getRoomInfo())
    			.deposit(propertyDTO.getDeposit())
    			.monthlyRent(propertyDTO.getMonthlyRent())
    			.maintenanceFee(propertyDTO.getMaintenanceFee())
    			.availableDate(propertyDTO.getAvailableDate())
    			.floor(propertyDTO.getFloor())
    			.shortDescription(propertyDTO.getShortDescription())
    			.longDescription(propertyDTO.getLongDescription())
    			.status(propertyDTO.getStatus())
    			.processingStatus("승인대기")
    			.registrationDate(new Timestamp(System.currentTimeMillis()))
    			.latitude(propertyDTO.getLatitude())
    			.longitude(propertyDTO.getLongitude())
    			.propertyOptions(options)
    			.propertyImageList(images)
    			.build();
    	
    	options.forEach(option -> option.setProperty(pe));
    	images.forEach(image -> image.setProperty(pe));
    	
    	this.propertyRepository.save(pe);
    	propertyImageRepository.saveAll(images);
    	propertyOptionRepository.saveAll(options);
    					
    }
    
    public Page<PropertyDTO> getPropertiesByStatus(String status, PageRequest pageRequest) {
        return propertyRepository.findByProcessingStatus(status, pageRequest)
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                });
    }
    
    @Transactional
    public void updatePropertyStatus(List<Long> propertyIds, String status) {
        propertyIds.forEach(id -> {
            PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid property ID: " + id));
            property.setProcessingStatus(status);
            propertyRepository.save(property);
        
        
        UserEntity user = userRepository.findById(property.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + property.getMemberId()));
            
            String title = "매물 상태 변경 알림";
            String message = "고객님의 매물(ID: " + property.getPropertyNum() + ")이 '" + status + "' 상태로 변경되었습니다.";
            alarmService.createNotificationForUser(user, title, message);
        });
    }
    
    public Page<PropertyDTO> getPropertiesByStatusAndKeyword(String status, String filterField, String keyword, PageRequest pageRequest) {
        return propertyRepository.findByStatusAndKeyword(status, filterField, keyword, pageRequest)
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                });
    }

    public Page<PropertyDTO> getPropertiesByKeyword(String filterField, String keyword, PageRequest pageRequest) {
        return propertyRepository.findByKeyword(filterField, keyword, pageRequest)
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                });
    }
    
    
    public Page<PropertyDTO> getPropertiesByMember(String username, PageRequest pageRequest) {
    	Optional<UserEntity> user = userRepository.findByUsername(username);
        return propertyRepository.findByMemberId(user.get().getMemberID(), pageRequest)
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                });
    }
    
    @Transactional
    public void updateProperty(Long propertyId, PropertyDTO propertyDTO) {
        // 기존 매물 정보 조회
        PropertyEntity existingProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid property ID: " + propertyId));
        
        // 기존 매물 정보를 DTO에서 가져온 값으로 업데이트
        existingProperty.setPropertyType(propertyDTO.getPropertyType());
        existingProperty.setPropertyAddress(propertyDTO.getPropertyAddress());
        existingProperty.setBuildingName(propertyDTO.getBuildingName());
        existingProperty.setSizePyeong(propertyDTO.getSizePyeong());
        existingProperty.setRoomInfo(propertyDTO.getRoomInfo());
        existingProperty.setDeposit(propertyDTO.getDeposit());
        existingProperty.setMonthlyRent(propertyDTO.getMonthlyRent());
        existingProperty.setMaintenanceFee(propertyDTO.getMaintenanceFee());
        existingProperty.setAvailableDate(propertyDTO.getAvailableDate());
        existingProperty.setFloor(propertyDTO.getFloor());
        existingProperty.setShortDescription(propertyDTO.getShortDescription());
        existingProperty.setLongDescription(propertyDTO.getLongDescription());
        existingProperty.setStatus(propertyDTO.getStatus());
        existingProperty.setProcessingStatus(propertyDTO.getProcessingStatus());
        existingProperty.setLatitude(propertyDTO.getLatitude());
        existingProperty.setLongitude(propertyDTO.getLongitude());
        
        // 기존 옵션 및 이미지 정보 삭제 후 새로 저장
        propertyOptionRepository.deleteAll(existingProperty.getPropertyOptions());
        propertyImageRepository.deleteAll(existingProperty.getPropertyImageList());

        List<PropertyOptionEntity> updatedOptions = propertyDTO.getPropertyOption().stream().map(optionDTO ->
            PropertyOptionEntity.builder()
                .heatingSystem(optionDTO.getHeatingSystem())
                .coolingSystem(optionDTO.getCoolingSystem())
                .livingFacilities(optionDTO.getLivingFacilities())
                .securityFacilities(optionDTO.getSecurityFacilities())
                .otherFacilities(optionDTO.getOtherFacilities())
                .parking(optionDTO.getParking())
                .elevator(optionDTO.getElevator())
                .propertyFeatures(optionDTO.getPropertyFeatures())
                .build()
        ).collect(Collectors.toList());

        List<PropertyImageEntity> updatedImages = propertyDTO.getPropertyImageList().stream().map(imageDTO ->
            PropertyImageEntity.builder()
                .imageOriginalName(imageDTO.getImageOriginalName())
                .imageStoredName(imageDTO.getImageStoredName())
                .build()
        ).collect(Collectors.toList());
        
        // 새 옵션 및 이미지 정보를 매물 엔티티에 설정
        updatedOptions.forEach(option -> option.setProperty(existingProperty));
        updatedImages.forEach(image -> image.setProperty(existingProperty));

        existingProperty.setPropertyOptions(updatedOptions);
        existingProperty.setPropertyImageList(updatedImages);
        
        // 변경된 매물 정보와 새 옵션 및 이미지 정보를 저장
        propertyRepository.save(existingProperty);
        propertyOptionRepository.saveAll(updatedOptions);
        propertyImageRepository.saveAll(updatedImages);
    }

}