package com.nz.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {
    private Long propertyId;
    private String propertyNum;
    private Long memberId;
    private String propertyType;
    private String propertyAddress;
    private String buildingName;
    private String sizePyeong;
    private String roomInfo;
    private Long price;
    private Long maintenanceFee;
    private Timestamp availableDate;
    private String floor;
    private String shortDescription;
    private String longDescription;
    private Timestamp registrationDate;
    private String status;
    private String processingStatus;
    private Double latitude;
    private Double longitude;
    private List<PropertyImageDTO> propertyImageList;
}