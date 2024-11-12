package com.be3c.sysmetic.global.common.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private int totalPageCount; // 전체 페이지 수
    private long totalItemCount; // 전체 데이터 수
    private int itemCountPerPage; // 한 페이지당 데이터 수
    private int currentPage; // 현재 페이지
    private List<T> list;
}