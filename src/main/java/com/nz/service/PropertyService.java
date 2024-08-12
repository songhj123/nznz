package com.nz.service;

import java.sql.Timestamp;
import java.util.List;
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
import com.nz.repository.PropertyImageRepository;
import com.nz.repository.PropertyOptionRepository;
import com.nz.repository.PropertyRepository;

import jakarta.transaction.Transactional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private PropertyOptionRepository propertyOptionRepository;
    
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
        });
    }
}