package com.nocountry.fintech.service;

import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransaccionService {
    @Autowired
    private TransaccionRepository repository;

    public Transaccion guardar(Transaccion t) { return repository.save(t); }
    public List<Transaccion> listarTodo() { return repository.findAll(); }
    public void eliminar(Long id) { repository.deleteById(id); }
}