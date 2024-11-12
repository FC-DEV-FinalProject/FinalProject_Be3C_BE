package com.be3c.sysmetic.global.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {
    private int currentPage;
    private int pageSize;
    private long totalElement;
    private int totalPages;
    private List<T> content;

    // Page 객체로부터 PageResponse를 생성하는 정적 팩토리 메서드
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent()
        );
    }
}
