package com.be3c.sysmetic.global.util.email.apiclient;

import com.be3c.sysmetic.global.util.email.dto.StibeeApiResponseDto;
import com.be3c.sysmetic.global.util.email.dto.StibeeSubscriberRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmailApiClient {

    private final WebClient webClientForAddressBook;

    @Value("${stibee.address.book.list.id.traider}")
    private String addressBookListIdTraider;

    /**
     * 가입한 트레이더 회원을 subscriber로 등록
     * @param stibeeSubscriberRequestDto subscriber 정보
     * @return 결과... 추후 비동기식으로 변경
     */
    public String addNewSubscriberRequest(StibeeSubscriberRequestDto stibeeSubscriberRequestDto) {

        try {
            String uri = "/lists/" + addressBookListIdTraider + "/subscribers";

            Mono<StibeeApiResponseDto> apiResponseMono = webClientForAddressBook.post()
                    .uri(uri)
                    .bodyValue(stibeeSubscriberRequestDto)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        System.out.println("Error response: " + response.statusCode());
                        return Mono.error(new RuntimeException("API call failed"));
                    })
                    .bodyToMono(StibeeApiResponseDto.class);

            StibeeApiResponseDto apiResponse = apiResponseMono.block(); // 동기적으로 기다림

            // 응답 처리
            if (apiResponse != null && apiResponse.isOk()) {

                return "성공: "+ apiResponse;
            } else {

                return apiResponse.toString();
            }

        }catch (WebClientResponseException e) {
            return "Error: " + e.getMessage() + " | Response body: " + e.getResponseBodyAsString();
        } catch (Exception e){
            return "Error: " + e.getMessage();
        }
    }

}