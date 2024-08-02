package com.nz.data;



import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContractDTO {
	private Long contractId;
	private PropertyDTO propertyId;
	private UserDTO landlordId;
	private UserDTO tenantId;
	private String stage;
	private Timestamp contractDate;
	private Timestamp expirationDate;

}
