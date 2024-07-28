package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.mapper.ConversionHistoryMapper;
import com.openpayd.forex.model.ConversionHistory;
import com.openpayd.forex.service.ConversionHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.when;

@WebMvcTest(ConversionHistoryController.class)
public class ConversionHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionHistoryService conversionHistoryService;

    @MockBean
    private ConversionHistoryMapper conversionHistoryMapper;

    @Test
    public void testGetConversionHistory_Success() throws Exception {
        ConversionHistory conversionHistory = new ConversionHistory();
        ConversionHistoryResponse response = new ConversionHistoryResponse();
        Page<ConversionHistory> page = new PageImpl<>(Collections.singletonList(conversionHistory), PageRequest.of(0, 10), 1);
        when(conversionHistoryService.getConversionHistory(null, null, 0, 10)).thenReturn(page);
        when(conversionHistoryMapper.toResponse(conversionHistory)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/conversations")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1));
    }
}
