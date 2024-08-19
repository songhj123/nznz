package com.nz.data;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class InquiryReplyDTO {

	private int replyId;
	private int inquiryId;
	private String content;
	private Timestamp createdTime;
}
