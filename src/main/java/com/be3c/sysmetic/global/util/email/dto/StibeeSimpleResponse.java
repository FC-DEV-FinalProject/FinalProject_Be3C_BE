package com.be3c.sysmetic.global.util.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter @ToString
public class StibeeSimpleResponse {

    @JsonProperty("Ok")
    private boolean ok;

    @JsonProperty("Error")
    private StibeeApiResponse.ApiError error;

    @JsonProperty("Value")
    private SimpleApiValue value;

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SimpleApiValue {
        private List<String> failDeny;
        private List<String> failUnknown;
        private List<String> failWrongEmail;
        private List<String> success;
        private List<String> update;
    }

}
