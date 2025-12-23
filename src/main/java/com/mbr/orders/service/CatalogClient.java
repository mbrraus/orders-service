package com.mbr.orders.service;

import com.mbr.orders.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CatalogClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogClient(RestTemplate restTemplate, @Value("${catalog.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ProductDto getBySku(String sku){
        try {
            return restTemplate.getForObject(baseUrl+"/products/by-sku/{sku}", ProductDto.class,sku);
        } catch(HttpClientErrorException.NotFound ex){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Unknown SKU: " + sku
            );
        }

    }

}
