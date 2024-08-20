package com.nz.controller;

import com.nz.service.VWorldApiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VWorldApiController {
    
    @Autowired
    private VWorldApiClientService vWorldApiClientService;
    
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam("query") String query) {
        try {
            List<String> suggestions = vWorldApiClientService.getSuggestions(query);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of("Error: " + e.getMessage()));
        }
    }
    

    @GetMapping("/coordinates")
    public ResponseEntity<?> getCoordinates(@RequestParam("address") String address) {
        try {
            double[] coordinates = vWorldApiClientService.getCoordinates(address);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage().contains("주소를 찾을 수 없습니다.")
                    ? "입력하신 주소를 찾을 수 없습니다. 다시 확인해 주세요."
                    : "좌표를 가져오는 중 오류가 발생했습니다. 다시 시도해 주세요.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }
}
