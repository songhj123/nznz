package com.nz.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("myProperty")
public class MyPropertyController {

	private final String uploadPath = "C:\\spring\\upload\\";
	private final PropertyService propertyService;
	private final UserService userService;
	
	@GetMapping("List")
	public String myPropertyList(@RequestParam(value = "page", defaultValue = "0") int page,
	   				             @RequestParam(value = "size", defaultValue = "10") int size,
					            Model model, Principal principal) {
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
	    @ModelAttribute @Valid PropertyDTO propertyDTO, 
	    BindingResult bindingResult, 
	    Model model, 
	    @RequestParam("propertyImageList") List<MultipartFile> propertyImageList, 
	    Principal principal) {

	    if (bindingResult.hasErrors()) {
	    	model.addAttribute("property", propertyDTO);
	        return "user/myPropertyUpdate"; 
	    }

	    if (propertyDTO.getPropertyOption() == null) {
	        propertyDTO.setPropertyOption(new ArrayList<>());
	    }

	    if (propertyDTO.getPropertyImageList() == null) {
	        propertyDTO.setPropertyImageList(new ArrayList<>());
	    }

	    for (MultipartFile mf : propertyImageList) {
	        if (!mf.isEmpty()) {
	            try {
	                String originalFilename = mf.getOriginalFilename();
	                Path filePath = Paths.get(uploadPath + originalFilename);
	                Files.write(filePath, mf.getBytes());
	                propertyDTO.getPropertyImageList().add(
	                        PropertyImageDTO.builder()
	                        .imageOriginalName(originalFilename)
	                        .imageStoredName(originalFilename)
	                        .build()
	                );
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    Long memberId = userService.getUserByMemberId(principal.getName());
	    propertyDTO.setMemberId(memberId);

	    propertyService.updateProperty(id, propertyDTO);

	    return "redirect:/myProperty/List"; 
	}


}
