package com.nocountry.fintech.controller;

import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {
    @Autowired
    private TransaccionService service;

    @GetMapping
    public List<Transaccion> obtenerTodas() { return service.listarTodo(); }

    @PostMapping
    public Transaccion crear(@RequestBody Transaccion t) { return service.guardar(t); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { 
        service.eliminar(id); 
    }

}