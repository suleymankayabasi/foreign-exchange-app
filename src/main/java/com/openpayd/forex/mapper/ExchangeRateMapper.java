package com.openpayd.forex.mapper;

import com.openpayd.forex.dto.ExchangeRateData;
import com.openpayd.forex.dto.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "sourceCurrency", target = "sourceCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "exchangeRate", target = "exchangeRate")
    @Mapping(source = "exchangeRateDate", target = "exchangeRateDate")
    ExchangeRateResponse toResponse(ExchangeRateData data);

}
