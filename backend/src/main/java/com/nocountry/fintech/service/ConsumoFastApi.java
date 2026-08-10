package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.AnalisisPerfilRequestDTO;
import com.nocountry.fintech.dto.response.AnalisisPerfilResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ConsumoFastApi {

    private final RestClient restClient;

    public ConsumoFastApi(@Value("${python.fastapi.url}") String baseUrl){
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public AnalisisPerfilResponseDTO recomendaciones(AnalisisPerfilRequestDTO analisisPerfilRequestDTO){
        return restClient.post()
                .uri("/api/v1/predict-health")
                .header("Content-Type", "application/json")
                .body(analisisPerfilRequestDTO)
                .retrieve()
                .body(AnalisisPerfilResponseDTO.class);
    }
}
