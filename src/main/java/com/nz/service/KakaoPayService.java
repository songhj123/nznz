package com.nz.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nz.data.ContractDTO;
import com.nz.data.KakaoPayDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Log
public class KakaoPayService {
private static final String Host = "https://open-api.kakaopay.com";

    
    private KakaoPayDTO kakaoPayDTO;
    private final ContractService contractService;

    public String kakaoPayReady(Long contractId) {
    	ContractDTO contractDTO = contractService.getContractById(contractId);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // 정확한 에러 파악을 위해 생성
        HttpHeaders headers = new HttpHeaders();
        		  
        headers.set("Authorization", "SECRET_KEY" + " " + "DEVD85F721355776767E1066FC4952EEA98460B9");
        headers.set("Content-type", "application/json");
        
        // Server Request Body : 서버 요청 본문
        Map<String, String> params = new HashMap<String, String>();

        params.put("cid", "TC0ONETIME"); // 가맹점 코드 - 테스트용
        params.put("partner_order_id", contractDTO.getPropertyId().getPropertyId().toString()); // 주문 번호  // 고유번호로 지정
        params.put("partner_user_id", contractDTO.getTenantId().getUsername()); // 로그인된 ID
        params.put("item_name", contractDTO.getPropertyId().getBuildingName()); // 상품 명  
        params.put("quantity", "1"); // 상품 수량
        params.put("total_amount", contractDTO.getPropertyId().getMonthlyRent()); // 상품 가격  
        params.put("tax_free_amount", "0"); // 상품 비과세 금액
        params.put("approval_url", "http://localhost:8080/kakao/kakaoPaySuccess/"+contractId); // 성공시 url
        params.put("cancel_url", "http://localhost:8080/kakao/cancel"); // 실패시 url
        params.put("fail_url", "http://localhost:8080/kakao/fail");

        // 헤더와 바디 붙이기
        HttpEntity<Map<String, String>> body = new HttpEntity<Map<String, String>>(params, headers);

        try {
            kakaoPayDTO = restTemplate.postForObject(new URI(Host + "/online/v1/payment/ready"), body, KakaoPayDTO.class);

            log.info(""+ kakaoPayDTO);
       
            return kakaoPayDTO.getNext_redirect_pc_url();
            
        } catch (RestClientException e) {
            e.printStackTrace();		
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
      
        return "/pay";
    }
    
    // 카카오페이 결제 승인
    // 사용자가 결제 수단을 선택하고 비밀번호를 입력해 결제 인증을 완료한 뒤, 최종적으로 결제 완료 처리를 하는 단계
    public void payApprove(String pgToken , Long contractId) {
    	ContractDTO contractDTO = contractService.getContractById(contractId);
        Map<String, String> parameters = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();     						
        headers.set("Authorization", "SECRET_KEY" + " " + "DEVD85F721355776767E1066FC4952EEA98460B9");
        headers.set("Content-type", "application/json");
        
        parameters.put("cid", "TC0ONETIME");              // 가맹점 코드(테스트용)
        parameters.put("tid", kakaoPayDTO.getTid());     // 결제 고유번호(자동생성) - DB저장 필요
        parameters.put("partner_order_id", contractDTO.getPropertyId().getPropertyId().toString()); // 주문번호
        parameters.put("partner_user_id", contractDTO.getTenantId().getUsername());    // 회원 아이디
        parameters.put("pg_token", pgToken);              // 결제승인 요청을 인증하는 토큰

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String, String>>(parameters, headers);

        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/approve";
        KakaoPayDTO approveResponse = template.postForObject(url, requestEntity, KakaoPayDTO.class);
        log.info("결제승인 응답객체: " + approveResponse);
    }
}
