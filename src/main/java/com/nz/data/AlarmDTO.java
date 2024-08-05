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

    private Long notificationId;
    private String title;
    private String content;
    private UserEntity memberId;
    private UserEntity receiverId;
    private Timestamp sentTime;
}