package com.be3c.sysmetic.global.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {
    private int currentPage;        // 현재 페이지
    private int pageSize;           // 한 페이지 크기
    private long totalElement;      // 전체 데이터 수
    private int totalPages;         // 전체 페이지 수
    private List<T> content;

    // Page 객체로부터 PageResponse를 생성하는 정적 팩토리 메서드
    // public static <T> PageResponse<T> of(Page<T> page) {
    //     return new PageResponse<>(
    //             page.getNumber(),
    //             page.getSize(),
    //             page.getTotalElements(),
    //             page.getTotalPages(),
    //             page.getContent()
    //     );
    // }
}
