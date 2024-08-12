package com.nz.data;

import java.sql.Timestamp;

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
public class AlarmReferenceDTO {
    private Long alarmRefId;
    private Long userId; // UserEntity 대신 userId로 간단하게 사용
    private Long alarmId; // AlarmEntity 대신 alarmId로 간단하게 사용
    private Integer isRead; // 0: unread, 1: read
    private Timestamp readAt;
}