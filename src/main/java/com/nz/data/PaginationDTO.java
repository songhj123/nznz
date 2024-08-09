package com.nz.data;

import lombok.Data;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private int pageCount;   // 총 페이지 수
    private int startPage;   // 페이지 블록의 시작 페이지
    private int pageBlock;   // 한 번에 표시할 페이지 수
    private int endPage;     // 페이지 블록의 마지막 페이지
    private int pageSize;    // 한 페이지에 표시할 게시물 수
    private int pageNum;     // 현재 페이지 번호
    private int start;       // 현재 페이지의 첫 번째 게시물 번호
    private int end;         // 현재 페이지의 마지막 게시물 번호
    private int count;       // 총 게시물 수
    private List<T> list;    // 현재 페이지의 게시물 리스트
}
