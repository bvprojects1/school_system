package com.bitsvalley.micro.controllers;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class RestController {

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/gab")
    public ResponseEntity<?> getOrganizations() {

        String url = "https://erestaupos.kapava.com/restaurantsolutions/public/api/organizations";

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("Authorization", "Basic " + "my_user_name:my_password");
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        System.out.println(responseEntity.getBody());
        return ResponseEntity.ok(responseEntity.getBody());

    }

}