package com.openpayd.forex.client;

import com.openpayd.forex.dto.FixerLatestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fixerClient", url = "${fixer.api.url}", fallback = FixerClientFallback.class)
public interface FixerClient {

    @GetMapping("/latest")
    FixerLatestResponse getLatestRates(@RequestParam("access_key") String accessKey);
}
