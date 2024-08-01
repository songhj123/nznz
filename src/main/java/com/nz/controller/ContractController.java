package com.nz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
	public String contractList(Model model) {
		List<ContractDTO> contractList = contractService.getAllContracts();
		model.addAttribute("contractList", contractList);
		return "admin/contractList";
	}
}
