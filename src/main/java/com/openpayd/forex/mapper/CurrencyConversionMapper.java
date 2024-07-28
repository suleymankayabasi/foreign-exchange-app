package com.openpayd.forex.mapper;

import com.openpayd.forex.dto.CurrencyConversionData;
import com.openpayd.forex.dto.CurrencyConversionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyConversionMapper {

    CurrencyConversionMapper INSTANCE = Mappers.getMapper(CurrencyConversionMapper.class);

    @Mapping(source = "transactionId", target = "transactionId")
    @Mapping(source = "convertedAmount", target = "convertedAmount")
    @Mapping(source = "transactionDate", target = "transactionDate")
    @Mapping(source = "sourceCurrency", target = "sourceCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "exchangeRate", target = "exchangeRate")
    CurrencyConversionResponse toResponse(CurrencyConversionData data);
}
