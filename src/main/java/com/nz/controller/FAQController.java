package com.nz.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.nz.data.FAQDTO;
import com.nz.data.PaginationDTO;
import com.nz.service.FAQService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/faq/*")
@RequiredArgsConstructor
public class FAQController {
	private final FAQService faqService;
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("writeForm")
	public String writeForm() {
		return "faq/writeForm";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("writePro")
	public String writePro(FAQDTO faqDTO) {
		faqService.createFaq(faqDTO);
		return "redirect:/faq/list";
	}
	
	@GetMapping("list")
	public String list(Model model,@RequestParam(name = "pageNum",defaultValue = "1")int pageNum) {
		 if (pageNum < 1) {
		        pageNum = 1;
		    }
		int pageSize = 10;
		PaginationDTO<FAQDTO> paginationDTO = faqService.getPaginatedList(pageNum, pageSize);
		model.addAttribute("paginationDTO", paginationDTO);	
		return "faq/list";
	}
	
	@GetMapping("detail/{faqId}")
	public String detail(@PathVariable("faqId")int faqId ,Model model) {
		FAQDTO faq = faqService.getFaqById(faqId);
		model.addAttribute("faq", faq);
		return "faq/detail";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("updateForm/{faqId}")
	public String updateForm(@PathVariable("faqId")int faqId, Model model) {
		FAQDTO faq = faqService.getFaqById(faqId);
		model.addAttribute("faq", faq);	
		return "faq/updateForm";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("updatePro")
	public String updatePro(@ModelAttribute FAQDTO faqDTO) {
		faqService.updateFaq(faqDTO);
		return "redirect:/faq/list";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("deletePro/{faqId}")
	public String deletePro(@PathVariable("faqId")int faqId) {
		return "redirect:/faq/list";
	}
}