package com.nz.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nz.data.PropertyDTO;
import com.nz.data.PropertyImageDTO;
import com.nz.service.PropertyService;
import com.nz.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("myProperty")
@PreAuthorize("isAuthenticated()")
public class MyPropertyController {

	private final String uploadPath = "C:\\spring\\upload\\";
	private final PropertyService propertyService;
	private final UserService userService;
	
	@GetMapping("list")
	public String myPropertyList(@RequestParam(value = "page", defaultValue = "0") int page,
	   				             @RequestParam(value = "size", defaultValue = "10") int size,
					            Model model, Principal principal) {
		
		 if (page < 0) {
		        page = 0;  // 페이지 번호가 음수인 경우 0으로 설정
		    }

		Page<PropertyDTO> properties = propertyService.getPropertiesByMember(principal.getName(), PageRequest.of(page, size));
		model.addAttribute("properties", properties);
		return "user/myPropertyList";
	}
	
	@GetMapping("/update/{id}")
	public String showEditPropertyPage(@PathVariable("id") Long id, Model model) {
        PropertyDTO property = propertyService.getPropertyById(id);
        model.addAttribute("property", property);
        return "user/myPropertyUpdate"; // 수정 페이지 템플릿
    }

	@PostMapping("/update/{id}")
	public String editProperty(
	    @PathVariable("id") Long id, 
	    @Valid PropertyDTO propertyDTO, 
	    BindingResult bindingResult, 
	    Model model, 
	    @RequestParam("propertyImageList") List<MultipartFile> propertyImageList, 
	    Principal principal) {

	    // 유효성 검사 오류가 있는지 확인하고, 있을 경우 업데이트 페이지로 다시 이동
	    if (bindingResult.hasErrors()) {
	        bindingResult.getAllErrors().forEach(error -> {
	            log.error("Validation error: {}", error.getDefaultMessage());
	        });
	        model.addAttribute("property", propertyDTO);
	        return "user/myPropertyUpdate"; 
	    }

	    // PropertyOption 초기화
	    if (propertyDTO.getPropertyOption() == null) {
	        propertyDTO.setPropertyOption(new ArrayList<>());
	    }

	    // PropertyImageList 초기화
	    if (propertyDTO.getPropertyImageList() == null) {
	        propertyDTO.setPropertyImageList(new ArrayList<>());
	    }

	    // MultipartFile 리스트를 PropertyImageDTO 리스트로 변환
	    for (MultipartFile mf : propertyImageList) {
	        if (!mf.isEmpty()) {
	            String originalFilename = null;
	            try {
	                originalFilename = mf.getOriginalFilename();
	                Path filePath = Paths.get(uploadPath + originalFilename);
	                Files.write(filePath, mf.getBytes());
	                propertyDTO.getPropertyImageList().add(
	                        PropertyImageDTO.builder()
	                        .imageOriginalName(originalFilename)
	                        .imageStoredName(originalFilename)
	                        .build()
	                );
	            } catch (Exception e) {
	                log.error("Error saving image file: {}", originalFilename, e);
	            }
	        }
	    }

	    Long memberId = userService.getUserByMemberId(principal.getName());
	    propertyDTO.setMemberId(memberId);

	    // 서비스에서 매물 업데이트
	    propertyService.updateProperty(id, propertyDTO);

	    return "redirect:/myProperty/list"; 
	}
	
	@GetMapping("deactivate/{id}")
	public String myPropertyDeactivate(@PathVariable("id") List<Long> id) {
		propertyService.updatePropertyStatus(id, "비활성화");
		return "redirect:/myProperty/list";
	}
	
	@GetMapping("activate/{id}")
	public String myPropertyActivate(@PathVariable("id") List<Long> id) {
		propertyService.updatePropertyStatus(id, "승인대기");
		return "redirect:/myProperty/list";
	}


}
