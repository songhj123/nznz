package com.nz.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.nz.data.NoticeDTO;
@Repository
@Mapper
public interface NoticeMapper {
	
	public int noticeInsert(NoticeDTO noticeDTO);
	public List<NoticeDTO> noticeList(@Param("start") int start, @Param("end") int end);
	public int countNotices();
	public NoticeDTO getNoticeById(@Param("noticeId")int noticeId);
	public void updateNotice(NoticeDTO noticeDTO);
	public void deleteNotice(@Param("noticeId") int noticeId);
	public void increaseViewCount(@Param("noticeId") int noticeId);
}
