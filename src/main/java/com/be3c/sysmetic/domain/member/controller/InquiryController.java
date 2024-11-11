package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.service.InquiryAnswerService;
import com.be3c.sysmetic.domain.member.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryAnswerService inquiryAnswerService;

    @GetMapping("/hello")
    public String hello() {
        return "hello!";
    }
}
