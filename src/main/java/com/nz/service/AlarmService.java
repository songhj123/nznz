package com.nz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nz.entity.AlarmEntity;
import com.nz.repository.AlarmRepository;

@Service
public class AlarmService {

	   @Autowired
	    private AlarmRepository alarmRepository;


}
