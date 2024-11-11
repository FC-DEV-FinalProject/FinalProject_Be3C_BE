package com.be3c.sysmetic.domain.member.repository;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InquirySearch {
    private String strategyKeyword;
    private String traderKeyword;
    private String questionerKeyword;
}
