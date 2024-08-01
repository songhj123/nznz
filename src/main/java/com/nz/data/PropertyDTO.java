package com.nz.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
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
    private String deposit; // 보증금
    private String monthlyRent; // 월세
    private Long maintenanceFee;
    private Date availableDate;
    private String floor;
    private String shortDescription;
    private String longDescription;
    private Timestamp registrationDate;
    private String status;
    private String processingStatus;
    private Double latitude;
    private Double longitude;
    private List<PropertyImageDTO> propertyImageList;
    private List<PropertyOptionDTO> propertyOption; // 추가된 필드
    private String agency;  // 추가된 필드
    private String contact;  // 추가된 필드
    private Integer likes;  // 추가된 필드
    private String username;
    private String name;
}