package com.nz.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nz.data.PropertyDTO;
import com.nz.data.PropertyImageDTO;
import com.nz.entity.PropertyEntity;
import com.nz.entity.PropertyImageEntity;
import com.nz.repository.PropertyImageRepository;
import com.nz.repository.PropertyRepository;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    public List<PropertyDTO> getAllProperties() {
        List<PropertyEntity> properties = propertyRepository.findAll();
        return properties.stream()
                .map(property -> {
                    List<PropertyImageEntity> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());
                    return convertToDTO(property, images);
                })
                .collect(Collectors.toList());
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

    private PropertyDTO convertToDTO(PropertyEntity propertyEntity, List<PropertyImageEntity> images) {
        List<PropertyImageDTO> imageDTOList = images.stream()
                .map(this::convertToImageDTO)
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
                .price(propertyEntity.getPrice())
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
                .build();
    }

    private PropertyImageDTO convertToImageDTO(PropertyImageEntity propertyImageEntity) {
        return PropertyImageDTO.builder()
                .imageId(propertyImageEntity.getImageId())
                .imageOriginalName(propertyImageEntity.getImageOriginalName())
                .imageStoredName(propertyImageEntity.getImageStoredName())
                .build();
    }
}
