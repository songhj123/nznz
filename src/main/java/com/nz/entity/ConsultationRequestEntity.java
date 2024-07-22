package com.nz.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultation_request_seq_gen")
    @SequenceGenerator(name = "consultation_request_seq_gen", sequenceName = "SEQ_CONSULTATION_REQUEST", allocationSize = 1)
    private Long requestId;

    private Long propertyId;

    private Long memberId;

    private Timestamp visitDate;

    private Timestamp requestDate;

    private String status;
}