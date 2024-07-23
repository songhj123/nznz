package com.nz.data;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequestDTO {
	private Long requestId;
	private Long propertyId;
	private String memberId;
	private String visitDate;
    private String visitTime;
	private Timestamp requestDate;
    private String status;
}