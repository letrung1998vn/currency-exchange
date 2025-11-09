package com.example.currency_exchange.mapper;

import com.example.currency_exchange.dto.CurrencyExchangeRateDto;
import com.example.currency_exchange.entity.CurrencyExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    @Mapping(source = "baseCurrency", target = "baseCurrency")
    @Mapping(source = "quoteCurrency", target = "quoteCurrency")
    @Mapping(source = "updateTime", target = "closeTime")
    @Mapping(source = "averageBid", target = "averageBid")
    @Mapping(source = "averageAsk", target = "averageAsk")
    @Mapping(source = "highBid", target = "highBid")
    @Mapping(source = "highAsk", target = "highAsk")
    @Mapping(source = "lowBid", target = "lowBid")
    @Mapping(source = "lowAsk", target = "lowAsk")
    CurrencyExchangeRateDto toDto(CurrencyExchangeRate entity);
}
