package com.nz.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class AlarmReferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_ref_seq_gen")
    @SequenceGenerator(name = "alarm_ref_seq_gen", sequenceName = "ALARM_REF_SEQ", allocationSize = 1)
    private Long alarmRefId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "alarm_id")
    private AlarmEntity alarm;

    @Column(nullable = false)
    private Integer isRead; // 0: unread, 1: read

    private Timestamp readAt;

}