package com.nz.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.ContractDTO;
import com.nz.service.ContractService;
import com.nz.service.PropertyService;

@Controller
public class ContractController {

	@Autowired
	private ContractService contractService;
	
	@Autowired
	private PropertyService propertyService;
	
	@GetMapping("contractProgress/{propertyId}")
	public String contractProgress(@PathVariable("propertyId") Long propertyId, Model model) {
		ContractDTO contract = contractService.getContractByPropertyId(propertyId);
		model.addAttribute("contract", contract);
		model.addAttribute("propertyDTO",propertyService.getPropertyById(propertyId));
		return "contract/contractProgress";
	}
	
	
	@GetMapping("/contractList")
	public String contractList(Model model, @RequestParam(name = "page", defaultValue = "0")int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ContractDTO> contractPage = contractService.getAllContracts(pageable);
		model.addAttribute("contractPage", contractPage);
		return "admin/contractList";
	}
	
	
	
    @GetMapping("/detail/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String contractDetail(@PathVariable Long contractId, Model model) {
        ContractDTO contract = contractService.getContractById(contractId);
        model.addAttribute("contract", contract);
        return "contract/contractDetail";
    }

    @GetMapping("/accept/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String acceptContract(@PathVariable Long contractId) {
        contractService.acceptContract(contractId);
        return "redirect:/contract/detail/" + contractId;
    }

    @GetMapping("/verifyDocuments/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String verifyDocuments(@PathVariable Long contractId) {
        contractService.verifyDocuments(contractId);
        return "redirect:/contract/detail/" + contractId;
    }

    @GetMapping("/applyAutomaticTransfer/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String applyAutomaticTransfer(@PathVariable Long contractId) {
        contractService.applyAutomaticTransfer(contractId);
        return "redirect:/contract/detail/" + contractId;
    }

    @PostMapping("/complete/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public String completeContract(@PathVariable Long contractId) {
        contractService.completeContract(contractId);
        return "redirect:/contract/detail/" + contractId;
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
}
