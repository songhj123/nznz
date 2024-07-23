package com.nz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nz.repository.DisinfectionStatusRepository;

@Service
public class DisinfectionStatusService {
	
	@Autowired
    private DisinfectionStatusRepository disinfectionStatusRepository;

}
