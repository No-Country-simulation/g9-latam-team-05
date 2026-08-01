package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.AnalisisPerfilRequestDTO;
import com.nocountry.fintech.dto.response.AnalisisPerfilResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ConsumoFastApi {

    private final RestClient restClient;

    public ConsumoFastApi(){
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8000")
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
