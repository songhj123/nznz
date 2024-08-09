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
public class AlarmEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_seq_gen")
	@SequenceGenerator(name = "alarm_seq_gen", sequenceName = "ALARM_SEQ", allocationSize = 1)
    private Long alarmId;

    private String title;
    private String message;
    private String username;

    private Timestamp createdAt;
}