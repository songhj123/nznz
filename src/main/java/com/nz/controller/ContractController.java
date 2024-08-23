package com.nz.controller;

import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.ContractDTO;
import com.nz.data.PropertyDTO;
import com.nz.data.UserDTO;
import com.nz.service.ContractService;
import com.nz.service.PropertyService;
import com.nz.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ContractController {


	private final ContractService contractService;
	private final PropertyService propertyService;
    private final UserService userService;
    
    
	@GetMapping("contractProgress/{propertyId}")
	public String contractProgress(@PathVariable("propertyId") Long propertyId, Model model) {
		ContractDTO contract = contractService.getContractByPropertyId(propertyId);
		model.addAttribute("contract", contract);
		model.addAttribute("propertyDTO",propertyService.getPropertyById(propertyId));
		return "contract/contractProgress";
	}
	
	
	@GetMapping("admin/contractList")
	public String contractList(Model model, @RequestParam(name = "page", defaultValue = "0")int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ContractDTO> contractPage = contractService.getAllContracts(pageable);
		model.addAttribute("contractPage", contractPage);
		return "admin/contractList";
	}
	
	
	
    @GetMapping("/detail/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String contractDetail(@PathVariable("contractId") Long contractId, Model model) {
        ContractDTO contract = contractService.getContractById(contractId);
        model.addAttribute("contract", contract);
        return "contract/contractDetail";
    }

    @PostMapping("/contract/accept/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String acceptContract(@PathVariable("contractId") Long contractId) {
        contractService.acceptContract(contractId);
        return "redirect:/contract/stage/" + contractId.toString();
    }

    @PostMapping("/contract/verifyDocuments/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String verifyDocuments(@PathVariable("contractId") Long contractId) {
        contractService.verifyDocuments(contractId);
        return "redirect:/contract/stage/" + contractId.toString();
    }

    @GetMapping("/contract/applyAutomaticTransfer/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String applyAutomaticTransfer(@PathVariable("contractId") Long contractId) {
        contractService.applyAutomaticTransfer(contractId);
        return "redirect:/contract/stage/" + contractId.toString();
    }

    @PostMapping("/contract/complete/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String completeContract(@PathVariable("contractId") Long contractId) {
        contractService.completeContract(contractId);
        return "redirect:/contract/stage/" + contractId.toString();
    }

    @GetMapping("/contract/{contractId}")
    public String contractDetail(@PathVariable("contractId") Long contractId, Model model, Principal principal) {
        String currentUsername = principal.getName();
        ContractDTO contract = contractService.getContractById(contractId);

        if (!isAuthorizedUser(contract, currentUsername)) {
            return "error/accessDenied";
        }

        model.addAttribute("contract", contract);
        return "contract/contractDetail";
    }
    
    private boolean isAuthorizedUser(ContractDTO contract, String username) {
        return contract.getLandlordId().getUsername().equals(username) || 
               contract.getTenantId().getUsername().equals(username);
    }
    
    
    
    
    @GetMapping("/contract")
    public String listContracts(Model model) {
        List<ContractDTO> contracts = contractService.getAllContracts();
        model.addAttribute("contracts", contracts);
        return "contract/contractList";
    }

    @GetMapping("/contract/myContracts")
    public String listMyContracts(Model model, Principal principal) {
        String currentUsername = principal.getName();
        List<ContractDTO> contracts = contractService.getContractsByUsername(currentUsername);
        model.addAttribute("contracts", contracts);
        return "contract/myContract";
    }





    @GetMapping("/contract/stage/{contractId}")
    public String contractStage(@PathVariable("contractId") Long contractId, Model model, Principal principal) {
        String currentUsername = principal.getName();
        ContractDTO contract = contractService.getContractById(contractId);

        if (!isAuthorizedUser(contract, currentUsername)) {
            return "error/accessDenied";
        }

        model.addAttribute("contract", contract);
        return "contract/contractStage";
    }



    @PostMapping("/contract/nextStage/{contractId}")
    public String nextStage(@PathVariable("contractId") Long contractId) {
        contractService.advanceToNextStage(contractId);
        return "redirect:/contract/stage/" + contractId.toString();
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(PropertyDTO.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Long propertyId = Long.parseLong(text);
                PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
                setValue(propertyDTO);
            }
        });

        binder.registerCustomEditor(UserDTO.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                UserDTO userDTO = userService.readUsername(text);
                setValue(userDTO);
            }
        });
    }
    
    @PostMapping("/contract/contracts")
    public String createContract(@RequestParam("propertyId") Long propertyId,
                                 @RequestParam("tenantId") String tenantUsername,
                                 @RequestParam("contractDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate contractDate,
                                 @RequestParam("expirationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expirationDate) {

        // UserDTO 조회
        UserDTO tenantDTO = userService.readUsername(tenantUsername);

        // PropertyDTO 조회
        PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);

        // ContractDTO에 필요한 정보 설정
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setTenantId(tenantDTO);
        contractDTO.setPropertyId(propertyDTO);
        
        // 날짜 설정
        contractDTO.setContractDate(Timestamp.valueOf(contractDate.atStartOfDay()));
        contractDTO.setExpirationDate(Timestamp.valueOf(expirationDate.atStartOfDay()));
        // Contract 생성 로직
        Long contractId = contractService.createContract(contractDTO);
        
        System.out.println(contractDTO);
        return "redirect:/contract/confirmation?contractId="+contractId;
    }

    @PostMapping("/contract/new")
    public String processContract(@RequestParam("propertyId") Long propertyId,
                                  @RequestParam("tenantId") String tenantId,
                                  Model model) {
        // 전달받은 propertyId와 tenantId를 모델에 추가
        model.addAttribute("propertyId", propertyId);
        model.addAttribute("tenantId", tenantId);

        // contractForm.html로 이동
        return "contract/contractForm";
    }

    @GetMapping("/contract/confirmation")
    public String showConfirmation(@RequestParam("contractId") Long contractId, Model model) {
        ContractDTO contract = contractService.getContractById(contractId);
        model.addAttribute("contract", contract);
        return "contract/confirmation";
    }
}