package com.openpayd.forex.controller;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.exception.InvalidInputException;
import com.openpayd.forex.exception.ResourceNotFoundException;
import com.openpayd.forex.service.ConversionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

public class ConversionHistoryControllerTest {

    @Mock
    private ConversionHistoryService conversionHistoryService;

    @InjectMocks
    private ConversionHistoryController conversionHistoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetConversionHistory_Success() {
        // Arrange
        ConversionHistoryResponse historyResponse = new ConversionHistoryResponse();
        List<ConversionHistoryResponse> historyList = Collections.singletonList(historyResponse);
        Page<ConversionHistoryResponse> page = new PageImpl<>(historyList, PageRequest.of(0, 10), 1);

        when(conversionHistoryService.getConversionHistory(anyString(), anyString(), anyInt(), anyInt())).thenReturn(page);

        // Act
        ResponseEntity<Page<ConversionHistoryResponse>> response = conversionHistoryController.getConversionHistory("12345", "2023-07-01 12:00:00", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    public void shouldGetConversionHistory_InvalidInput() {
        // Arrange
        when(conversionHistoryService.getConversionHistory(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new InvalidInputException("Invalid input"));

        // Act
        ResponseEntity<Page<ConversionHistoryResponse>> response = conversionHistoryController.getConversionHistory("12345", "2023-07-01 12:00:00", 0, 10);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldGetConversionHistory_ResourceNotFound() {
        // Arrange
        when(conversionHistoryService.getConversionHistory(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new ResourceNotFoundException("Resource not found"));

        // Act
        ResponseEntity<Page<ConversionHistoryResponse>> response = conversionHistoryController.getConversionHistory("12345", "2023-07-01 12:00:00", 0, 10);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldGetConversionHistory_UnexpectedError() {
        // Arrange
        when(conversionHistoryService.getConversionHistory(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<Page<ConversionHistoryResponse>> response = conversionHistoryController.getConversionHistory("12345", "2023-07-01 12:00:00", 0, 10);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
