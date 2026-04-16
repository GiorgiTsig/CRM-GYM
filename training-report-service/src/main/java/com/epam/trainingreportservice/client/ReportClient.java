package com.epam.trainingreportservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "report-service", url = "http://localhost:8081")
public interface ReportClient {

    @RequestMapping("/greeting")
    String greeting();
}
