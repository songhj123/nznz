package com.nz.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nz.data.NoticeDTO;
import com.nz.data.PaginationDTO;
import com.nz.repository.NoticeMapper;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class NoticeService {
	private final NoticeMapper noticeMapper;
	
	public void createNotice(NoticeDTO noticeDTO) {		
		noticeDTO.setCreatedDate(new Date());

		noticeMapper.noticeInsert(noticeDTO);
	}
	
	public PaginationDTO<NoticeDTO> getPaginatedList(int pageNum, int pageSize){
		int currentPage = pageNum;
		int start = (currentPage-1) * pageSize + 1;
		int end = currentPage * pageSize;
		int count = noticeMapper.countNotices();
		
		List<NoticeDTO> list = null;
		if(count>0) {
			list = noticeMapper.noticeList(start, end);
		}
		
		int pageCount = (int)Math.ceil((double)count/pageSize);
		int pageBlock = 10;
		int startPage = ((currentPage-1)/pageBlock) * pageBlock + 1;
		int endPage = startPage + pageBlock-1;
		if(endPage>pageCount) endPage = pageCount;
		
		PaginationDTO<NoticeDTO> paginationDTO = new PaginationDTO<>();
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
		return noticeMapper.countNotices();
	}
	
	public List<NoticeDTO> getList(int start, int end){
		
		return noticeMapper.noticeList(start, end);
	}
	
	public NoticeDTO getNoticeById(int noticeId) {
		noticeMapper.increaseViewCount(noticeId);
		return noticeMapper.getNoticeById(noticeId);
	}
	
	public void updateNotice(NoticeDTO noticeDTO) {
		noticeMapper.updateNotice(noticeDTO);
	}
	
	public void deleteNotice(int noticeId) {
		noticeMapper.deleteNotice(noticeId);
	}
}
