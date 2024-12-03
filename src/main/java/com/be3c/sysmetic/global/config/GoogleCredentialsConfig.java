package com.be3c.sysmetic.global.config;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
@Slf4j
public class GoogleCredentialsConfig {

    @Value("${google.credentials.json}")
    private String googleCredentialsJson;

    private GoogleCredentials createGoogleCredentials() throws IOException {
        // JSON 문자열을 InputStream으로 변환
        byte[] decodedBytes = Base64.getDecoder().decode(googleCredentialsJson);
        InputStream credentialsStream = new ByteArrayInputStream(decodedBytes);
        return GoogleCredentials.fromStream(credentialsStream);
    }

    @Bean
    public BetaAnalyticsDataClient betaAnalyticsDataClient() throws IOException {
        GoogleCredentials credentials = createGoogleCredentials();
        // BetaAnalyticsDataClient 생성 및 반환
        BetaAnalyticsDataSettings settings = BetaAnalyticsDataSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        // BetaAnalyticsDataClient를 설정으로 초기화
        return BetaAnalyticsDataClient.create(settings);
    }
}