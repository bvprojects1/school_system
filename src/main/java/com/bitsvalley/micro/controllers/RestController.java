package com.bitsvalley.micro.controllers;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Controller
public class RestController {

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/momo")
    public ResponseEntity<?> getOrganizations() {

//        String url = "https://erestaupos.kapava.com/restaurantsolutions/public/api/organizations";

        //get the collection auth token.
        String tokenEndpoint = "https://sandbox.momodeveloper.mtn.com/collection/token";

        String apiUser = "a24fa60e-4238-445b-81c0-b266972e6df3";
        String apiKey = "4793e92622d44799860d68afd3b62152";

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("Ocp-Apim-Subscription-Key", "df3ded9380234a1d9d62dbf2ecb78cd3");
//        requestHeaders.add("Authorization", "Basic " + apiUser + ":" + apiKey);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, requestEntity, String.class);
        if(responseEntity.getStatusCodeValue()==302){
            URI location = responseEntity.getHeaders().getLocation();
            responseEntity = restTemplate.exchange(location.toString(), HttpMethod.POST, requestEntity, String.class);

        }
        System.out.println(responseEntity.getBody());
        System.out.println(responseEntity.getStatusCode());
        return ResponseEntity.ok(responseEntity.getBody());

    }

}