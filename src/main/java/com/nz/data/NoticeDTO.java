package com.nz.data;

import java.util.Date;

import lombok.Data;

@Data
public class NoticeDTO {
	private int noticeId;
	private String title;
	private String content;
	private Date createdDate;
	private int viewCount;
}
