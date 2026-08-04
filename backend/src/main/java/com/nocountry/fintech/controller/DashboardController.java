package com.nocountry.fintech.controller;

import com.nocountry.fintech.dto.response.ResumenKpiDTO;
import com.nocountry.fintech.service.DashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashBoardService dashBoardService;

    public DashboardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/resumen/{id}")
    public ResponseEntity<ResumenKpiDTO> obtenerResumen(@PathVariable Long id){
        ResumenKpiDTO resumenKpiDTO = dashBoardService.obtenerResumen(id);

        return ResponseEntity.ok(resumenKpiDTO);
    }

}
