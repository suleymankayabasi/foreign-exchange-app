package com.openpayd.forex.client;

import com.openpayd.forex.dto.CurrencyLayerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencyLayerClient", url = "${currency-layer.api.url}", fallback = CurrencyLayerClientFallback.class)
public interface CurrencyLayerClient {

    @GetMapping("/live")
    CurrencyLayerResponse getLiveRates(@RequestParam("access_key") String accessKey);
}
