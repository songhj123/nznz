package com.nz.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nz.data.DisinfectionStatusDTO;
import com.nz.data.PropertyDTO;
import com.nz.data.PropertyImageDTO;
import com.nz.data.UserDTO;
import com.nz.service.ContractService;
import com.nz.service.DisinfectionStatusService;
import com.nz.service.PropertyService;
import com.nz.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PropertyController {
    // 파일 업로드 경로
    private final String uploadPath = "C:\\spring\\upload\\"; 
    
    private final PropertyService propertyService;
    private final UserService userService;
    private final DisinfectionStatusService disinfectionStatusService;

    @GetMapping("/properties")
    public String getProperties(Model model) {
        List<PropertyDTO> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "property/properties"; // 변경된 경로로 수정
    }

    @GetMapping("/properties/within")
    @ResponseBody
    public ResponseEntity<List<PropertyDTO>> getPropertiesWithin(
            @RequestParam("southWestLat") double southWestLat,
            @RequestParam("southWestLng") double southWestLng,
            @RequestParam("northEastLat") double northEastLat,
            @RequestParam("northEastLng") double northEastLng) {
        List<PropertyDTO> properties = propertyService.getPropertiesWithin(southWestLat, southWestLng, northEastLat, northEastLng);
        
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/")
    public String home(Model model) {
        return "home/home"; // 변경된 경로로 수정
    }
    
    @GetMapping("/property/{id}")
    public String getPropertyDetails(@PathVariable("id") Long id, Model model) {
        PropertyDTO property = propertyService.getPropertyById(id);
        if (property == null) {
            return "redirect:/properties";  // 매물을 찾을 수 없을 경우 목록 페이지로 리디렉션
        }
        
        // 최신 방역 상태를 가져와 모델에 추가
        DisinfectionStatusDTO disinfectionStatus = disinfectionStatusService.getLatestDisinfectionStatus();
        model.addAttribute("disinfectionStatus", disinfectionStatus);
        
        model.addAttribute("property", property);
        return "property/propertyDetails"; // 변경된 경로로 수정
    }
    
    @GetMapping("/sellForm")
    public String sellProperty(Model model) {
        model.addAttribute("propertyDTO", new PropertyDTO());
        return "property/sellForm"; // 변경된 경로로 수정
    }
    
    @GetMapping("/roomDetail")
    public String roomDetail() {
        return "property/roomDetail"; // 변경된 경로로 수정
    }
    
    @PostMapping("/sellPro")
    public String sellPro(@Valid PropertyDTO propertyDTO, BindingResult bindingResult,
            Model model, @RequestParam("propertyImageList") List<MultipartFile> propertyImageList,
            Principal principal) {

        // 파일 처리 로직 추가
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
        this.propertyService.createProperty(propertyDTO);
        model.addAttribute("property", propertyDTO);
        return "property/sellPro"; // 변경된 경로로 수정
    }
    
    @GetMapping("/display")
    public ResponseEntity<Resource> display(@RequestParam("filename")String filename){
        try {
            Path filePath = Paths.get(uploadPath + filename);
            
            if (!Files.exists(filePath)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Resource resource = new FileSystemResource(filePath.toString());
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(filePath));
            return new ResponseEntity<>(resource, header, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/admin/propertyList")
    public String adminPropertyList(Model model, 
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "status", required = false) String status,
                                    @RequestParam(value = "filterField", required = false) String filterField,
                                    @RequestParam(value = "searchKeyword", required = false) String keyword) {
        Page<PropertyDTO> properties;

        if ((status != null && !status.isEmpty()) && (filterField != null && !filterField.isEmpty()) && (keyword != null && !keyword.isEmpty())) {
            // 상태와 키워드 필터가 모두 적용되는 경우
            properties = propertyService.getPropertiesByStatusAndKeyword(status, filterField, keyword, PageRequest.of(page, size));
        } else if (status != null && !status.isEmpty()) {
            // 상태 필터만 적용되는 경우
            properties = propertyService.getPropertiesByStatus(status, PageRequest.of(page, size));
        } else if ((filterField != null && !filterField.isEmpty()) && (keyword != null && !keyword.isEmpty())) {
            // 키워드 필터만 적용되는 경우
            properties = propertyService.getPropertiesByKeyword(filterField, keyword, PageRequest.of(page, size));
        } else {
            // 필터가 적용되지 않은 경우
            properties = propertyService.getAllPropertiesPaged(PageRequest.of(page, size));
        }
        
        model.addAttribute("properties", properties);
        model.addAttribute("currentStatus", status); // 현재 필터링 상태 유지
        model.addAttribute("filterField", filterField);
        model.addAttribute("searchKeyword", keyword);
        return "admin/propertyList";
    }
    
    @PostMapping("/admin/property/updateStatus")
    public String updatePropertyStatus(@RequestParam("selectedProperties") List<Long> propertyIds, 
                                       @RequestParam("action") String action, 
                                       RedirectAttributes redirectAttributes) {
    	
    	// 로그 추가
        log.info("Received propertyIds: {}", propertyIds);
        log.info("Received action: {}", action);
        
        try {
            if ("approve".equals(action)) {
                propertyService.updatePropertyStatus(propertyIds, "승인");
            } else if ("reject".equals(action)) {
                propertyService.updatePropertyStatus(propertyIds, "반려");
            } else if ("delete".equals(action)) {
                propertyService.updatePropertyStatus(propertyIds, "비활성화"); // 실제 삭제가 아니라 상태 변경
            }
            redirectAttributes.addFlashAttribute("message", "상태가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상태 업데이트 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/propertyList";
    }
    
    
    
    @GetMapping("/properties/location")
    public String getPropertiesByLocation(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            Model model) {
        // 지도 페이지로 이동할 때 초기 중심 좌표로 설정
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        return "property/properties"; // 지도 페이지로 이동
    }
}
