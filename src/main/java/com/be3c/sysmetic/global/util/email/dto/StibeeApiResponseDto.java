package com.be3c.sysmetic.global.util.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * 스티비에서 응답하는 양식
 */
@ToString
@Setter @Getter
public class StibeeApiResponseDto {

    @JsonProperty("Ok")
    private boolean ok;

    @JsonProperty("Error")
    private ApiError erro;

    @JsonProperty("Value")
    private ApiValue value;


    @ToString
    @Getter @Setter
    public static class ApiError {
        private String Code;
        private int HttpStatusCode;
        private String Message;
    }

    @ToString
    @Getter @Setter
    public static class ApiValue {
        private List<Subscriber> failDeny;
        private List<Subscriber> failUnknown;
        private List<Subscriber> failWrongEmail;
        private List<Subscriber> success;
        private List<Subscriber> update;
    }
}
