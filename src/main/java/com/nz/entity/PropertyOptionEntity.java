package com.nz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class PropertyOptionEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long optionId;
   // 부모 엔티티 참조
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "property_id")
   private PropertyEntity property;
   
   private String heatingSystem;
   private String coolingSystem;
   private String livingFacilities;
   private String securityFacilities;
   private String otherFacilities;
   private Boolean parking;
   private Boolean elevator;
   private String propertyFeatures;
}
