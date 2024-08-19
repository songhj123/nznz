package com.nz.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nz.data.InquiryDTO;
import com.nz.data.InquiryReplyDTO;
import com.nz.data.NoticeDTO;
import com.nz.data.PaginationDTO;
import com.nz.repository.InquiryMapper;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class InquiryService {
	private final InquiryMapper inquiryMapper;
	
	public void createInquiry(InquiryDTO inquiryDTO) {
		inquiryDTO.setCreatedTime(new Timestamp(System.currentTimeMillis()));
		
		inquiryMapper.inquiryInsert(inquiryDTO);
	}
	
	public PaginationDTO<InquiryDTO> getPaginationList(int pageNum, int pageSize){
		int currentPage = pageNum;
		int start = (currentPage-1) * pageSize + 1;
		int end = currentPage * pageSize;
		int count = inquiryMapper.countInquiry();
		
		List<InquiryDTO> list = null;
		if(count>0) {
			list = inquiryMapper.inquiryList(start, end);
		}
		
		int pageCount = (int)Math.ceil((double)count/pageSize);
		int pageBlock = 10;
		int startPage = ((currentPage-1)/pageBlock) * pageBlock + 1;
		int endPage = startPage + pageBlock-1;
		if(endPage>pageCount) endPage = pageCount;
		
		PaginationDTO<InquiryDTO> paginationDTO = new PaginationDTO<>();
		paginationDTO.setPageCount(pageCount);
		paginationDTO.setStartPage(startPage);
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
		return inquiryMapper.countInquiry();
	}
	
	public List<InquiryDTO> getList(int start, int end){
		return inquiryMapper.inquiryList(start, end);
	}
	
	public InquiryDTO getInquiryById(int inquiryId) {
	 	return inquiryMapper.getInquiryById(inquiryId);
	} 
	
	public void updateInquiry(InquiryDTO inquiryDTO) {
		inquiryMapper.updateInquiry(inquiryDTO);
	}
	
	public void deleteInquiry(int inquiryId) {
		inquiryMapper.deleteInquiry(inquiryId);
	}
	
	 // 답변 저장 메서드
    public void saveReply(InquiryDTO inquiryDTO) {
        // 필요한 데이터 처리 로직을 추가할 수 있습니다.
        inquiryDTO.setCreatedTime(new Timestamp(System.currentTimeMillis())); // 예시: 현재 시간을 설정

        // 답변 데이터를 데이터베이스에 저장
        inquiryMapper.insertReply(inquiryDTO);
    }
    
    public InquiryDTO getInquiryWithRepliesById(int inquiryId) {
        InquiryDTO inquiry = inquiryMapper.getInquiryById(inquiryId);
        // memberId가 null인 경우 null을 반환하여 화면에 표시되지 않도록 처리
        if (inquiry.getMemberId() == null) {
            return null;
        }
        
        List<InquiryReplyDTO> replies = inquiryMapper.getRepliesByInquiryId(inquiryId);
        inquiry.setReplies(replies); // 답변 목록 설정       
        return inquiry;
    }
    
    public List<InquiryDTO> getAllInquiries() {
        List<InquiryDTO> inquiries = inquiryMapper.getAllInquiries();
        return inquiries.stream()
                        .filter(inquiry -> inquiry.getMemberId() != null)
                        .collect(Collectors.toList());
    }
}
