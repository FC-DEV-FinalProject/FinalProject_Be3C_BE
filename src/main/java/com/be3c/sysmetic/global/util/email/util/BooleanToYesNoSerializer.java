package com.be3c.sysmetic.global.util.email.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BooleanToYesNoSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean isAdConsent, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (isAdConsent == null) {
            gen.writeString("Y");
        } else {
            gen.writeString(isAdConsent ? "Y" : "N");
        }
    }
}