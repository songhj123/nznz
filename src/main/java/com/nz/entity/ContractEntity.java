package com.nz.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contract")  // 데이터베이스 테이블 이름을 명시적으로 지정합니다.
public class ContractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_seq_gen")
    @SequenceGenerator(name = "contract_seq_gen", sequenceName = "SEQ_CONTRACT", allocationSize = 1)
    @Column(name = "CONTRACTID")
    private Long contractId;

    @Column(name = "PROPERTYID")
    private Long propertyId;

    @Column(name = "LANDLORDID")
    private Long landlordId;

    @Column(name = "TENANTID")
    private Long tenantId;

    @Column(name = "STAGE")
    private String stage;

    @Column(name = "CONTRACTDATE")
    private Timestamp contractDate;

    @Column(name = "EXPIRATIONDATE")
    private Timestamp expirationDate;
}
