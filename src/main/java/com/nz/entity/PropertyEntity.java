package com.nz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    @Column(unique = true)
    private String propertyNum;

    private Long memberId;
    private String propertyType;
    private String propertyAddress;
    private String buildingName;
    private String sizePyeong;
    private String roomInfo;
    private Long price;
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

    @OneToMany(mappedBy = "property", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PropertyImageEntity> propertyImageList;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyOptionEntity> propertyOptions;
    
    @PrePersist
    public void generatePropertyNum() {
        this.propertyNum = "PROP-" + this.propertyId;
    }
}

