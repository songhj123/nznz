package com.nz.controller;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nz.data.ContractDTO;
import com.nz.service.KakaoPayService;

@Controller
@RequiredArgsConstructor
@Log
@RequestMapping("/kakao/*")
public class KakaoPayController {
	
	private final KakaoPayService kakaoPay;

    @GetMapping("/kakaoPay")
    public String kakaoPayGet() {
    	return "kakaoPay";
    }

    @PostMapping("/kakaoPay/{contractId}")
    public String kakaoPay(@PathVariable("contractId")Long contractId){
    	
        return "redirect:" + kakaoPay.kakaoPayReady(contractId);
    }

    @GetMapping("/kakaoPaySuccess/{contractId}")
    public String kakaoPaySuccess(@RequestParam("pg_token")String pgToken,@PathVariable("contractId")Long contractId,Model model) {
        kakaoPay.payApprove(pgToken,contractId);
        return "redirect:/contract/applyAutomaticTransfer/"+contractId;
    }
    
    @GetMapping("/completed")
    @ResponseBody
    public String complate() {
    	return "success";
    }
    
    
    @GetMapping("/cancel")
    public String  cancel() {
    	return "kakaoPayCancel";
    }
   
   
}








