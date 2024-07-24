package com.nz.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisinfectionStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "disinfectionStatusSeq")
    @SequenceGenerator(name = "disinfectionStatusSeq", sequenceName = "SEQ_DISINFECTION_STATUS", allocationSize = 1)
    @Column(name = "level_id")
    private Long levelId;

    @Column(name = "DISINFECTION_LEVEL", nullable = false)
    private Integer disinfectionLevel;

    @Column(name = "reason_title", length = 50)
    private String reasonTitle;

    @Column(name = "reason_details", length = 50)
    private String reasonDetails;

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
