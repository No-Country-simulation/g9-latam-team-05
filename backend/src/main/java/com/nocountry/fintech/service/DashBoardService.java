package com.nocountry.fintech.service;

import com.nocountry.fintech.dto.response.ResumenKpiDTO;
import com.nocountry.fintech.exception.ResourceNotFoundException;
import com.nocountry.fintech.model.PerfilesFinancieros;
import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.repository.PerfilesFinancierosRepository;
import com.nocountry.fintech.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DashBoardService {

    private final PerfilesFinancierosRepository perfilesFinancierosRepository;
    private final TransaccionRepository transaccionRepository;

    @Autowired
    public DashBoardService (PerfilesFinancierosRepository perfilesFinancierosRepository,
                             TransaccionRepository transaccionRepository){
        this.perfilesFinancierosRepository = perfilesFinancierosRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public ResumenKpiDTO obtenerResumen(Long id){
        PerfilesFinancieros perfil = perfilesFinancierosRepository.findByUsuarioId(id).orElseThrow(() ->
                new ResourceNotFoundException("Perfil financiero no encontada para el usuario: " + id));

        List<Transaccion> transacciones = transaccionRepository.findAllByUsuarioId(id);

        BigDecimal totalIngresos = transacciones.stream()
                .filter(t -> "INGRESO".equalsIgnoreCase(t.getTipo()))
                .map(Transaccion::getMonto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal totalGastos = transacciones.stream()
                .filter(t -> "GASTO".equalsIgnoreCase(t.getTipo()))
                .map(Transaccion::getMonto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal ingresoMensual= perfil.getIngresoMensual();

        BigDecimal balanceNeto = perfil.getIngresoMensual().subtract(totalGastos);

        Double tasaAhorro = 0.0;

        if(ingresoMensual.compareTo(BigDecimal.ZERO) > 0){
            tasaAhorro = balanceNeto
                    .divide(ingresoMensual,4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .doubleValue();
        }

        return new ResumenKpiDTO(
                ingresoMensual,
                totalGastos,
                balanceNeto,
                tasaAhorro
        );
    }
}
