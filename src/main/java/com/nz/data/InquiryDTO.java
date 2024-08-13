package com.nz.data;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class InquiryDTO {
	private int inquiryId;
	private Long memberId;
	private String type;
	private String title;
	private String content;
	private String email;
	private Timestamp createdTime;
	private List<InquiryReplyDTO> replies;
}
