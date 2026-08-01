package com.nocountry.fintech;

import com.nocountry.fintech.dto.request.TransaccionRequestDto;
import com.nocountry.fintech.dto.request.UsuarioRequestDto;
import com.nocountry.fintech.dto.response.TransaccionResponseDto;
import com.nocountry.fintech.dto.response.UsuarioResponseDto;
import com.nocountry.fintech.model.Categoria;
import com.nocountry.fintech.repository.CategoriaRepository;
import com.nocountry.fintech.service.TransaccionService;
import com.nocountry.fintech.service.UsuarioService;
import com.nocountry.fintech.util.SchemaInspector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

// ====================================================================================================
// NOTA DE ARQUITECTURA: 
// Se añadió @SpringBootTest.
// Se eliminó implements CommandLineRunner para evitar que se ejecute al iniciar la app de producción.
// ====================================================================================================

@Disabled("Inhabilitado hasta la integración con Oracle Cloud")
@SpringBootTest
public class DatabaseTest {

    @Autowired
    private SchemaInspector schemaInspector;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    public void testFlujoCompletoBaseDeDatos() {

        System.out.println("--- INICIANDO PRUEBA DE INTEGRIDAD Y CRUD ---");

        /*
         * -------------------------------------------------------------
         * PRUEBAS NATIVAS DE ORACLE
         * -------------------------------------------------------------
        */

        /*
        schemaInspector.verificarConexionOracle();
        schemaInspector.listarTablasDelEsquema();
        schemaInspector.listarColumnasDeTodasLasTablas();
        schemaInspector.limpiarTabla("TRANSACCIONES");
        schemaInspector.limpiarTabla("USUARIOS");
        */

        // Limpieza
        schemaInspector.limpiarTabla("TRANSACCIONES");
        schemaInspector.limpiarTabla("USUARIOS");
        schemaInspector.limpiarTabla("CATEGORIAS");

        // Creacion de categoria
        Categoria categoria = new Categoria();
        categoria.setNombre("Alimentación"); 
        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        // Registrar usuario de prueba
        UsuarioRequestDto usuarioDto = new UsuarioRequestDto();
        usuarioDto.setNombre("Carlos Test");
        usuarioDto.setEmail("carlos.test@nocountry.com");
        usuarioDto.setPassword("password123");

        UsuarioResponseDto usuarioCreado = usuarioService.registrarUsuario(usuarioDto);

        Assertions.assertNotNull(usuarioCreado, "El usuario no pudo ser creado.");
        Assertions.assertNotNull(usuarioCreado.getId(), "El ID del usuario generado no debe ser nulo.");

        schemaInspector.listarColumnasDeTabla("USUARIOS");

        // Crear y asociar transacción
        TransaccionRequestDto transaccionDto = new TransaccionRequestDto();
        transaccionDto.setUsuarioId(usuarioCreado.getId());
        transaccionDto.setCategoriaId(categoriaGuardada.getId());
        transaccionDto.setDescripcion("Gasto de prueba integridad");
        transaccionDto.setMonto(BigDecimal.valueOf(150.00));
        transaccionDto.setTipo("GASTO");
        transaccionDto.setFecha(LocalDateTime.now());

        TransaccionResponseDto transaccionCreada = transaccionService.guardar(transaccionDto);

        Assertions.assertNotNull(transaccionCreada, "La transacción no pudo ser guardada.");
        Assertions.assertNotNull(transaccionCreada.getId(), "El ID de la transacción no debe ser nulo.");

        System.out.println("Transacción creada y vinculada al usuario ID: " + usuarioCreado.getId());

        schemaInspector.listarDatosDeTabla("TRANSACCIONES");
        long totalUsuarios = schemaInspector.contarRegistros("USUARIOS");
        long totalTransacciones = schemaInspector.contarRegistros("TRANSACCIONES");

        Assertions.assertEquals(1, totalUsuarios, "Debe existir exactamente 1 usuario en la BD.");
        Assertions.assertEquals(1, totalTransacciones, "Debe existir exactamente 1 transacción en la BD.");

        /*
         * -------------------------------------------------------------
         * PRUEBA DE ELIMINACIÓN EN CASCADA
         * -------------------------------------------------------------
         * 
         * 
         * 
        */

        /*
        usuarioService.eliminarUsuario(usuarioCreado.getId());
        schemaInspector.listarDatosDeTabla("USUARIOS");
        schemaInspector.listarDatosDeTabla("TRANSACCIONES");

        long usuariosDespuesDelBorrado = schemaInspector.contarRegistros("USUARIOS");
        long transaccionesDespuesDelBorrado = schemaInspector.contarRegistros("TRANSACCIONES");

        Assertions.assertEquals(0, usuariosDespuesDelBorrado, 
            "El usuario debió ser eliminado completamente de la BD.");
        Assertions.assertEquals(0, transaccionesDespuesDelBorrado, 
            "Las transacciones asociadas debieron eliminarse en cascada.");
        
        */

        /*
        // REINICIAR SECUENCIA
        schemaInspector.reiniciarTablaConSecuencia("USUARIOS", "USUARIOS_SEQ");
        schemaInspector.reiniciarTablaConSecuencia("TRANSACCIONES", "TRANSACCIONES_SEQ");
        System.out.println("--- Secuencias restablecidas a su estado inicial con éxito ---");
        */

    }
}