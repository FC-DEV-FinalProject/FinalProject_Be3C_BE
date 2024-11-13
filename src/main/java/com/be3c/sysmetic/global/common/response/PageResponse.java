package com.be3c.sysmetic.global.common.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private Integer totalPageCount; // 전체 페이지 수
    private Long totalItemCount; // 전체 데이터 수
    private Integer itemCountPerPage; // 한 페이지당 데이터 수
    private Integer currentPage; // 현재 페이지
    private List<T> list;
}