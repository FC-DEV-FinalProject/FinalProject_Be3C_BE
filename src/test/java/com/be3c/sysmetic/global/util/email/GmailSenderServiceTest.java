package com.be3c.sysmetic.global.util.email;

import com.be3c.sysmetic.global.util.email.service.GmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class GmailSenderServiceTest {

    @Autowired
    private GmailSenderService gmailSenderService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    private String testText;
    private String expectedSubject;

    @BeforeEach
    void setUp() {
        // given
        testText = "This is a test email for manual request.";
        expectedSubject = "[스티비] This is a test email for manual request.";
    }

    @Test
    void sendManualRequest_shouldSendEmailSuccessfully() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(adminEmail);  // 발신자
        message.setTo(adminEmail);  // 수신자
        message.setSubject(expectedSubject);  // 제목
        message.setText(testText);  // 본문 내용

        // when: 메일 발송
        gmailSenderService.sendEmailToAdmin(expectedSubject, testText);

        // then: 메일 발송이 실제로 이루어졌는지 확인 (직접 하기)
        assertThat(message.getSubject()).isEqualTo(expectedSubject);
        assertThat(message.getTo()[0]).isEqualTo(adminEmail);
    }
}
