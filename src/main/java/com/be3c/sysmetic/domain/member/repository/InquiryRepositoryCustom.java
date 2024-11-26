package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryRepositoryCustom {

    Page<Inquiry> adminInquirySearchWithBooleanBuilder(InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto, Pageable pageable);

    Page<Inquiry> inquirySearchWithBooleanBuilder(InquiryListShowRequestDto inquiryListShowRequestDto, Pageable pageable);
}
