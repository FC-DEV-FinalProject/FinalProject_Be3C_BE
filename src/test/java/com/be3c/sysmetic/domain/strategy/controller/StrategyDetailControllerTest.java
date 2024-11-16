package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.StrategyDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StrategyDetailController.class)
public class StrategyDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StrategyDetailService strategyDetailService;

    @Test
    void getStrategyDetailTest() throws Exception {
        // Given
        Long invalidId = 999L;
        when(strategyDetailService.getDetail(invalidId))
                .thenThrow(new NoSuchElementException("해당 전략의 상세 보기 페이지가 존재하지 않습니다."));

        // Then
        mockMvc.perform(get("/strategy/detail")
                .param("id", invalidId.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
