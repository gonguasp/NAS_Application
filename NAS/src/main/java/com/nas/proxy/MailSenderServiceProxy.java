package com.nas.proxy;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "mail-sender-service", url = "localhost:8000")
public interface MailSenderServiceProxy {

    @PostMapping("/sendEmail")
    @ResponseBody
    @Headers("Content-Type: application/json")
    ResponseEntity<HttpStatus> sendEmail(@RequestBody String jwt, @RequestParam String operation);
}