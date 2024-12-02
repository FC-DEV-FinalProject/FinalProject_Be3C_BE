package com.be3c.sysmetic.global.util.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;


    public void sendEmailToAdmin(String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(adminEmail);  // 발신자 이메일
        message.setTo(adminEmail);                       // 수신자 이메일
        message.setSubject(subject);             // 이메일 제목
        message.setText(text);                   // 이메일 내용

        try {
            System.out.println("자바 메일 센더 실행 : ");
            javaMailSender.send(message);
        } catch (Exception e) {
            try{
                javaMailSender.send(message);
            } catch (Exception ex) {
                log.error("오류 이메일 발송에 실패: {}", ex);
            }
        }
    }
}
