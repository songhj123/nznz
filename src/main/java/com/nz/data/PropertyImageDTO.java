package com.nz.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyImageDTO {
    private Long imageId;
	private Long propertyId;
    private String imageOriginalName;
    private String imageStoredName;
}