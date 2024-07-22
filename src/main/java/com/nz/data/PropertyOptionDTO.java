package com.nz.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PropertyOptionDTO {
   private String heatingSystem;
   private String coolingSystem;
   private String livingFacilities;
   private String securityFacilities;
   private String otherFacilities;
   private Boolean parking;
   private Boolean elevator;
   private String propertyFeatures;
}