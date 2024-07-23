package com.nz.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

import com.nz.data.PropertyDTO;
import com.nz.data.PropertyImageDTO;
import com.nz.service.PropertyService;

import jakarta.validation.Valid;

@Controller
public class PropertyController {
    // 파일 업로드 경로
    private final String uploadPath = "C:\\spring\\upload\\"; 

    @Autowired
    private PropertyService propertyService;

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
        for (PropertyDTO property : properties) {
            System.out.println("==========================================");
            System.out.println("Property ID: " + property.getPropertyId());
            System.out.println("Property Images: " + property.getPropertyImageList());
        }
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
            Model model, @RequestParam("propertyImageList") List<MultipartFile> propertyImageList) {

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
}
