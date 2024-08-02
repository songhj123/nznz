package com.nz.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROPERTYID")
    private PropertyEntity property;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LANDLORDID")
    private UserEntity landlord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TENANTID")
    private UserEntity tenant;

    @Column(name = "STAGE")
    private String stage;

    @Column(name = "CONTRACTDATE")
    private Timestamp contractDate;

    @Column(name = "EXPIRATIONDATE")
    private Timestamp expirationDate;
}
