package com.joo.digimon.card.externel;

import com.joo.digimon.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(url = "${feign.client.config.scaling-client.url}",name = "scalingClient", configuration = FeignClientConfig.class)
public interface ScalingClient {

    @PostMapping("/api")
    byte[] upscaleImage(
            @RequestParam(value = "url") String url,
            @RequestParam(value ="scale") int scale,
            @RequestParam(value ="noise") int noise,
            @RequestParam(value ="style") String style);
}
