package com.nz.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nz.data.AlarmDTO;
import com.nz.entity.AlarmEntity;
import com.nz.entity.AlarmReferenceEntity;
import com.nz.entity.UserEntity;
import com.nz.repository.AlarmReferenceRepository;
import com.nz.repository.AlarmRepository;
import com.nz.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
    
    private final AlarmRepository ALARMREPOSITORY;
    private final AlarmReferenceRepository ALARMREFERENCEREPOSITORY;
    private final UserRepository USERREPOSITORY;

    public void createNotificationForAll(AlarmDTO alarmDTO) {
        AlarmEntity alarm = AlarmEntity.builder()
                .title(alarmDTO.getTitle())
                .message(alarmDTO.getMessage())
                .username(alarmDTO.getUsername())  // 발신자 설정
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        ALARMREPOSITORY.save(alarm);
    }
    
    public Page<AlarmDTO> getAllNotifications(Pageable pageable) {
        Page<AlarmEntity> alarms = ALARMREPOSITORY.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")));
        return alarms.map(alarm -> AlarmDTO.builder()
                        .alarmId(alarm.getAlarmId())
                        .title(alarm.getTitle())
                        .message(alarm.getMessage())
                        .username(alarm.getUsername())
                        .createdAt(alarm.getCreatedAt())
                        .build());
    }
    
    public void createNotificationForUser(UserEntity userId, String title, String message) {
    	UserEntity user = USERREPOSITORY.findById(userId.getMemberID())
    			.orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));
    	
    	AlarmEntity alarm = AlarmEntity.builder()
    			.title(title)
    			.message(message)
    			.username(user.getUsername())
    			.createdAt(new Timestamp(System.currentTimeMillis()))
    			.build();
    	
    	ALARMREPOSITORY.save(alarm);
    	
    	AlarmReferenceEntity alarmReference = AlarmReferenceEntity.builder()
    			.user(user)
    			.alarm(alarm)
    			.isRead(0)
    			.build();
    	
    	ALARMREFERENCEREPOSITORY.save(alarmReference);
    }
}