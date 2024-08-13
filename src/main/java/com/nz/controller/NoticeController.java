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

import com.nz.data.NoticeDTO;
import com.nz.data.PaginationDTO;
import com.nz.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Controller
@RequestMapping("/notice/*")
@RequiredArgsConstructor
public class NoticeController {
	private final NoticeService noticeService;
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("writeForm")
	public String writeForm() {
		return "notice/writeForm";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("writePro")
	public String writePro(NoticeDTO noticeDTO) {
		noticeService.createNotice(noticeDTO);
		return "redirect:/notice/list";
	}
	
	@GetMapping("list")
	public String list(Model model,@RequestParam(name = "pageNum",defaultValue = "1")int pageNum) {
		int pageSize = 10;
		PaginationDTO<NoticeDTO> paginationDTO = noticeService.getPaginatedList(pageNum, pageSize);
		model.addAttribute("paginationDTO", paginationDTO);
		return "notice/list";
	}
	
	@GetMapping("detail/{noticeId}")
	public String detail(@PathVariable("noticeId")int noticeId, Model model) {
		NoticeDTO notice = noticeService.getNoticeById(noticeId);
		model.addAttribute("notice", notice);
		return "notice/detail";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("updateForm/{noticeId}")
	public String updateForm(@PathVariable("noticeId")int noticeId, Model model) {
		NoticeDTO notice = noticeService.getNoticeById(noticeId);
		model.addAttribute("notice", notice);
		return "notice/updateForm";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("updatePro")
	public String updatePro(@ModelAttribute NoticeDTO noticeDTO) {
		noticeService.updateNotice(noticeDTO);
		return "redirect:/notice/list";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("deletePro/{noticeId}")
	public String deletePro(@PathVariable("noticeId")int noticeId) {
		noticeService.deleteNotice(noticeId);
		return "redirect:/notice/list";
	}
	

}
