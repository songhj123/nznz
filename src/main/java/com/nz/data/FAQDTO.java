package com.nz.data;

import lombok.Data;

@Data
public class FAQDTO {
	private int faqId;
	private String question;
	private String answer;
	private String author;
}
