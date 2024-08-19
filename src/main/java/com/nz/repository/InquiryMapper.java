package com.nz.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.nz.data.InquiryDTO;
import com.nz.data.InquiryReplyDTO;
@Repository
@Mapper
public interface InquiryMapper {

	public int inquiryInsert(InquiryDTO inquiryDTO);
	public List<InquiryDTO> inquiryList(@Param("start") int start, @Param("end") int end);
	public int countInquiry();
	public InquiryDTO getInquiryById(@Param("inquiryId")int inquiryId);
	public void updateInquiry(InquiryDTO inquiryDTO);
	public void deleteInquiry(@Param("inquiryId")int inquiryId);
	// 답변을 데이터베이스에 저장하는 메서드
    void insertReply(InquiryDTO inquiryDTO);
    // 답변 목록 가져오기
    List<InquiryReplyDTO> getRepliesByInquiryId(@Param("inquiryId") int inquiryId); 
    List<InquiryDTO> getAllInquiries();
}
