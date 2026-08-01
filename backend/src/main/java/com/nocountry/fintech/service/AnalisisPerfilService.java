package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.request.AnalisisPerfilRequestDTO;
import com.nocountry.fintech.dto.response.AnalisisPerfilResponseDTO;
import com.nocountry.fintech.model.AnalisisHistorial;
import org.springframework.stereotype.Service;

@Service
public class AnalisisPerfilService {

    private final ConsumoFastApi consumoFastApi;


    public AnalisisPerfilService(ConsumoFastApi consumoFastApi) {
        this.consumoFastApi = consumoFastApi;
    }

    public AnalisisPerfilResponseDTO analisis(AnalisisPerfilRequestDTO analisisPerfilRequestDTO){
        return consumoFastApi.recomendaciones(analisisPerfilRequestDTO);
    }

    public void crearAnalisis(AnalisisPerfilResponseDTO analisisPerfilResponseDTO){
        AnalisisHistorial historial = new AnalisisHistorial();
    }
}
