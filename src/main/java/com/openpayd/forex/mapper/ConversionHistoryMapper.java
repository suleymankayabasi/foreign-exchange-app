package com.openpayd.forex.mapper;

import com.openpayd.forex.dto.ConversionHistoryResponse;
import com.openpayd.forex.model.ConversionHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConversionHistoryMapper {

    @Mapping(source = "transactionId", target = "transactionId")
    @Mapping(source = "sourceCurrency", target = "sourceCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "convertedAmount", target = "convertedAmount")
    @Mapping(source = "transactionDate", target = "transactionDate")
    ConversionHistoryResponse toResponse(ConversionHistory conversionHistory);
}
