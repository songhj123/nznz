package com.nz.data;

import java.sql.Timestamp;

import com.nz.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {
    private Long alarmId;
    private String title;
    private String message;
    private String username;
    private Timestamp createdAt;
}