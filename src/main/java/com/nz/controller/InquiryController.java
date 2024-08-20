package com.nz.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.InquiryDTO;
import com.nz.data.PaginationDTO;
import com.nz.data.UserDTO;
import com.nz.service.InquiryService;
import com.nz.service.UserService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RequestMapping("/inquiry/*")
@Controller
public class InquiryController {
	private final InquiryService inquiryService;
	private final UserService userService;
	
	@GetMapping("writeForm")
	@PreAuthorize("isAuthenticated()")
	public String writeForm(Model model,Principal principal) {
		 String username = principal.getName();
	     UserDTO user = userService.readUsername(username);
	     InquiryDTO inquiryDTO = new InquiryDTO();
         inquiryDTO.setMemberId(user.getMemberId());
         model.addAttribute("inquiryDTO", inquiryDTO);
		return "inquiry/writeForm";
	}
	
	@PostMapping("writePro")
	public String writePro(@ModelAttribute InquiryDTO inquiryDTO, Principal principal) {
		 String username = principal.getName();
	     UserDTO user = userService.readUsername(username);
	     inquiryDTO.setMemberId(user.getMemberId());
		
		inquiryService.createInquiry(inquiryDTO);
		return "redirect:/inquiry/list";
	}
	
	@GetMapping("list")
	public String list(Model model,@RequestParam(name = "pageNum",defaultValue = "1")int pageNum) {
		int pageSize = 10;
		PaginationDTO<InquiryDTO> paginationDTO = inquiryService.getPaginationList(pageNum, pageSize);
		model.addAttribute("paginationDTO", paginationDTO);
		return "inquiry/list";
	}
	
	@GetMapping("detail/{inquiryId}")
	public String detail(@PathVariable("inquiryId") int inquiryId, Principal principal, Model model) {
	    InquiryDTO inquiry = inquiryService.getInquiryWithRepliesById(inquiryId);
	    model.addAttribute("inquiry", inquiry);

	    if (principal != null) {
	    	Long memberId = userService.getUserByMemberId(principal.getName());
	        model.addAttribute("memberId",memberId);
	    }
	    return "inquiry/detail";
	}
	
	@GetMapping("updateForm/{inquiryId}")
	@PreAuthorize("isAuthenticated()")
	public String updateForm(@PathVariable("inquiryId")int inquiryId, Model model,Principal principal) {
		String username = principal.getName();
		UserDTO user = userService.readUsername(username);
		InquiryDTO inquiry = inquiryService.getInquiryById(inquiryId);
		if (!inquiry.getMemberId().equals(user.getMemberId())) {
	        throw new RuntimeException("자신의 문의만 수정할 수 있습니다.");
	    }
		model.addAttribute("inquiry", inquiry);
		return "inquiry/updateForm";
	}
	
	@PostMapping("updatePro")
	@PreAuthorize("isAuthenticated()")
	public String updatePro(@ModelAttribute InquiryDTO inquiryDTO, Principal principal) {
		String username = principal.getName();
	    UserDTO user = userService.readUsername(username);
	    inquiryDTO.setMemberId(user.getMemberId());
		inquiryService.updateInquiry(inquiryDTO);
		return "redirect:/inquiry/list";
	}
	
	@PostMapping("deletePro/{inquiryId}")
	@PreAuthorize("isAuthenticated()")
	public String deletePro(@PathVariable("inquiryId")int inquiryId, Principal principal) {
		String username = principal.getName();
	    UserDTO user = userService.readUsername(username);
	    InquiryDTO inquiry = inquiryService.getInquiryById(inquiryId);
		
	 // 로그인한 사용자의 memberId와 삭제하려는 InquiryDTO의 memberId를 비교
	    if (inquiry.getMemberId().equals(user.getMemberId())) {
	        // 작성자와 일치하면 삭제
	        inquiryService.deleteInquiry(inquiryId);
	    } else {
	        // 작성자가 아니면 에러 처리 (예: 권한이 없음 메시지 반환 등)
	        throw new RuntimeException("삭제 권한이 없습니다.");
	    }
		return "redirect:/inquiry/list";
	}
	
	@PostMapping("replyPro")
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public String replyPro(@ModelAttribute InquiryDTO inquiryDTO) {
	    inquiryService.saveReply(inquiryDTO);
	    return "redirect:/inquiry/detail/" + inquiryDTO.getInquiryId();
	}
			
}
