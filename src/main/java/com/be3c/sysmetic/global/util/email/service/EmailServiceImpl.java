package com.be3c.sysmetic.global.util.email.service;

import com.be3c.sysmetic.global.config.security.RedisUtils;
import com.be3c.sysmetic.global.util.email.apiclient.EmailApiClient;
import com.be3c.sysmetic.global.util.email.dto.*;
import com.be3c.sysmetic.global.util.email.exception.EmailSendingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailApiClient emailApiClient;
    private final RedisUtils redisUtils;

    @Override
    public StibeeApiResponse addUserSubscriberRequest(SubscriberRequest subscriberRequest) {
        try {
            return emailApiClient.addUserSubscriberRequest(subscriberRequest);
        } catch (Exception e) {
            handleException(e, "일반회원 구독 요청 실패");
        }
        return null;
    }

    @Override
    public StibeeApiResponse addTraderSubscriberRequest(SubscriberRequest subscriberRequest) {
        try {
            return emailApiClient.addTraderSubscriberRequest(subscriberRequest);
        } catch (Exception e) {
            handleException(e, "트레이더 구독 요청 실패");
        }
        return null;
    }

    @Override
    public StibeeApiResponse updateUserSubscriberRequest(SubscriberRequest subscriberRequest) {
        try {
            subscriberRequest.setEventOccurredBy(EventOccuredBy.MANUAL);
            return emailApiClient.addUserSubscriberRequest(subscriberRequest);
        } catch (Exception e) {
            handleException(e, "일반회원 구독 요청 실패");
        }
        return null;
    }

    @Override
    public StibeeApiResponse updateTraderSubscriberRequest(SubscriberRequest subscriberRequest) {
        try {
            subscriberRequest.setEventOccurredBy(EventOccuredBy.MANUAL);
            return emailApiClient.addTraderSubscriberRequest(subscriberRequest);
        } catch (Exception e) {
            handleException(e, "트레이더 구독 요청 실패");
        }
        return null;
    }

    @Override
    public StibeeApiResponse deleteUserSubscriberRequest(List<String> emails) {
        try {
            return emailApiClient.deleteUserSubscriberRequest(emails);
        } catch (Exception e) {
            handleException(e, "일반회원 구독 삭제 실패");
        }
        return null;
    }

    @Override
    public StibeeApiResponse deleteTraderSubscriberRequest(List<String> emails) {
        try {
            return emailApiClient.deleteTraderSubscriberRequest(emails);
        } catch (Exception e) {
            handleException(e, "트레이더 구독 삭제 실패");
        }
        return null;
    }

    // 자동 email API -------------------------------------------------------------------------

    @Override
    public void sendAndSaveAuthCode(String toEmail) {
        try {
            emailApiClient.addTempSubscriberRequest(SubscriberRequest.builder()
                    .subscribers(List.of(Subscriber.builder().email(toEmail).build()))
                    .build());

            String authCode = generateVerificationCode();
            String response = emailApiClient.sendAuthEmailRequest(new AuthCodeRequest(toEmail, authCode));

            if (!"ok".equals(response)) {
                log.error("이메일 인증코드 발송 실패: 응답 'ok'가 아님 응답: {}", response);
                throw new EmailSendingException("이메일 인증코드 발송 실패: 응답 'ok'가 아님 응답: " + response);
            }

            redisUtils.saveEmailAuthCodeWithExpireTime(toEmail, authCode, TimeUnit.HOURS.toMillis(1));
            emailApiClient.deleteTempSubscriberRequest(List.of(toEmail));
        } catch (Exception e) {
            handleException(e, "이메일 인증코드 발송 중 오류 발생");
        }
    }

    private String generateVerificationCode() {
        int code = 100000 + ThreadLocalRandom.current().nextInt(900000); // 6자리 랜덤 숫자
        return String.valueOf(code);
    }

    @Override
    public void notifyStrategyInquiryRegistration(InquiryRequest inquiryRequestDto) {
        try {
            emailApiClient.sendInquiryRegistrationEmailRequest(inquiryRequestDto);
        } catch (Exception e) {
            handleException(e, "문의 등록 알람 이메일 발송 실패");
        }
    }

    @Override
    public void notifyStrategyInterestRegistration(InterestRequest interestRequest) {
        try {
            emailApiClient.sendInterestRegistrationEmailRequest(interestRequest);
        } catch (Exception e) {
            handleException(e, "관심 등록 알람 이메일 발송 실패");
        }
    }

    private void handleException(Exception e, String message) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException webClientException = (WebClientResponseException) e;
            log.error("{} 발생: 상태 코드 = {}, 메시지 = {}", message,
                    webClientException.getStatusCode().value(),
                    webClientException.getResponseBodyAsString());
        } else if (e instanceof IllegalArgumentException) {
            log.error("{} 실패: 잘못된 요청 값입니다. 메시지: {}", message, e.getMessage());
        } else if (e instanceof IOException) {
            log.error("{} 실패: 파일 처리 중 오류 발생. 메시지: {}", message, e.getMessage());
        } else if (e instanceof RedisConnectionException) {
            log.error("{} 실패: Redis 연결 오류 발생. 메시지: {}", message, e.getMessage());
        } else if (e instanceof JsonProcessingException) {
            log.error("{} 실패: JSON 직렬화 오류 발생. 메시지: {}", message, e.getMessage());
        } else {
            log.error("{} 실패: {}", message, e.getMessage(), e);
        }
        throw new EmailSendingException(message, e);
    }

}
