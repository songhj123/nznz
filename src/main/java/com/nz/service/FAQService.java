package com.nz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nz.data.FAQDTO;
import com.nz.data.PaginationDTO;
import com.nz.repository.FAQMapper;
import com.nz.repository.NoticeMapper;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class FAQService {
	private final FAQMapper faqMapper;
	
	public void createFaq(FAQDTO faqDTO) {
		faqMapper.faqInsert(faqDTO);
	}
	
	public PaginationDTO<FAQDTO> getPaginatedList(int pageNum, int pageSize){
		int currentPage = pageNum;
		int start = (currentPage-1) * pageSize + 1;
		int end = currentPage * pageSize;
		int count = faqMapper.countFaqs();
				
		List<FAQDTO> list = null;
		if(count>0) {
			list = faqMapper.faqList(start, end);
		}
		
		int pageCount = (int)Math.ceil((double)count/pageSize);
		int pageBlock = 10;
		int startPage = ((currentPage-1)/pageBlock) * pageBlock + 1;
		int endPage = startPage + pageBlock-1;
		if(endPage>pageCount) endPage = pageCount;
		
		PaginationDTO<FAQDTO> paginationDTO = new PaginationDTO<>();
		paginationDTO.setPageCount(pageCount);
		paginationDTO.setStart(startPage);
		paginationDTO.setEndPage(endPage);
		paginationDTO.setPageBlock(pageBlock);
		paginationDTO.setPageSize(pageSize);
		paginationDTO.setPageNum(pageNum);
		paginationDTO.setStart(start);
		paginationDTO.setEnd(end);
		paginationDTO.setCount(count);
		paginationDTO.setList(list);
		
		return paginationDTO;
	}
	
	public int countAll() {
		return faqMapper.countFaqs();
	}
	
	public List<FAQDTO> getFaq(int start, int end){
		return faqMapper.faqList(start, end);					
	}
	
	public FAQDTO getFaqById(int faqId) {
		return faqMapper.getFaqById(faqId);
	}
	
	public void updateFaq(FAQDTO faqDTO) {
		faqMapper.updateFaq(faqDTO);
	}
	
	public void deleteFaq(int faqId) {
		faqMapper.deleteFaq(faqId);
	}
}
