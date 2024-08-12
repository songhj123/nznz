package com.nz.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.nz.data.FAQDTO;
@Repository
@Mapper
public interface FAQMapper {

	public int faqInsert(FAQDTO faqDTO);
	public List<FAQDTO> faqList(@Param("start")int start, @Param("end")int end);
	public int countFaqs();
	public FAQDTO getFaqById(@Param("faqId")int faqId);
	public void updateFaq(FAQDTO faqDTO);
	public void deleteFaq(@Param("faqId")int faqId);
}
