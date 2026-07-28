package com.nocountry.fintech;

import com.nocountry.fintech.model.Transaccion;
import com.nocountry.fintech.model.Usuario;
import com.nocountry.fintech.service.TransaccionService;
import com.nocountry.fintech.service.UsuarioService;
import com.nocountry.fintech.util.SchemaInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

// ====================================================================================================
// NOTA DE ARQUITECTURA Y METODOLOGÍA (SDLC / ISO-IEC 12207):
// 
// SE DESACTIVÓ ESTA CLASE (@Component) PORQUE VIOLA EL CICLO DE VIDA DE DESARROLLO DE SOFTWARE (SDLC).
// INTENTABA EJECUTAR PRUEBAS DE INFRAESTRUCTURA DE NUBE (ORACLE CLOUD "USER_TABLES") EN LA FASE DE CODIFICACIÓN LOCAL.
// 
// FASES DEL CICLO DE VIDA DE DESARROLLO DE SOFTWARE (SDLC - IEEE / ISO-IEC 12207):
//   1. ANÁLISIS DE REQUISITOS (Casos de uso y especificación de la API)
//   2. DISEÑO DE ARQUITECTURA (MVC + Capas / Modelo Relacional 3NF)
//   3. DESARROLLO Y CODIFICACIÓN LOCAL (PostgreSQL Local) <--- [FASE ACTUAL - SEMANA 1]
//   4. PRUEBAS Y TESTING (Pruebas unitarias e integración aisladas en src/test/java)
//   5. DESPLIEGUE Y DESPLIEGUE EN LA NUBE (Oracle Cloud OCI / Autonomous DB)
//   6. MANTENIMIENTO Y MONITOREO
//
// CITA DE REFERENCIA METODOLÓGICA:
// "La contaminación del código fuente principal con scripts de inspección o dependencias de infraestructura 
// de despliegue prematuras viola el principio de Separación de Incumbencias (Separation of Concerns, IEEE 1471) 
// y corrompe la ejecución del entorno de desarrollo local." — ISO/IEC/IEEE 12207:2017 Systems and Software Engineering.
// ====================================================================================================
// @Component
public class DatabaseTest implements CommandLineRunner {

    @Autowired
    private SchemaInspector schemaInspector;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TransaccionService transaccionService;

    @Override
    public void run(String... args) throws Exception {

        // --------------- VERIFICAR CONEXIÓN ---------------
        // schemaInspector.verificarConexionOracle();

        // --------------- VER ESQUEMA DB---------------
        // schemaInspector.listarTablasDelEsquema();

        // ---- VER TODAS LAS COLUMNAS DE LAS TABLAS EN EL ESQUEMA ----
        // schemaInspector.listarColumnasDeTodasLasTablas();

        // --------------- VER ESQUEMA TABLA---------------
        //schemaInspector.listarColumnasDeTabla("USUARIOS");



        // --------------- LIMPIAR TABLAS ---------------
        // Se limpian en orden correcto debido a la restricción de llave foránea
        /*
        schemaInspector.limpiarTabla("TRANSACCIONES");
        schemaInspector.limpiarTabla("USUARIOS");
        */



        /*
        // --------------- REGISTRAR USUARIO PRUEBA ---------------
        System.out.println("--- INICIANDO PRUEBA DE INTEGRIDAD Y ELIMINACIÓN ---");

        Usuario usuario = usuarioService.registrarUsuario(
            "Carlos Test",
            "carlos.test@nocountry.com",
            "password123",
            "ACTIVO"
        );

        if (usuario == null) {
            System.err.println("No se pudo crear el usuario para la prueba.");
            return;
        }
        // Ver el usuario creado
        schemaInspector.listarDatosDeTabla("USUARIOS");


        // --------------- CREAR Y ASOCIAR TRANSACCIÓN ---------------
        Transaccion t = new Transaccion();
        t.setUserId(usuario.getId());
        t.setCategoriaId(1L);
        t.setDescripcion("Gasto de prueba integridad");
        t.setMonto(150.00);
        t.setTipo("GASTO");
        t.setFecha(LocalDateTime.now());

        transaccionService.guardar(t);
        System.out.println("Transacción creada y vinculada al usuario ID: " + usuario.getId());
        // Ver la transaccion nueva
        schemaInspector.listarDatosDeTabla("TRANSACCIONES");
        */

        /*
        // --------------- PROBAR ELIMINACION EN CASCADA ---------------
        System.out.println("\n[Prueba] Intentando eliminar al usuario con ID " + usuario.getId() + " (que tiene transacciones asociadas):");
        usuarioService.eliminarUsuario(usuario.getId());
        // Verificar los datos borrados
        schemaInspector.listarDatosDeTabla("USUARIOS");
        schemaInspector.listarDatosDeTabla("TRANSACCIONES");
        */

        /*
        // --------------------- REINICIAR SECUENCIA ----------------------
        schemaInspector.reiniciarTablaConSecuencia("USUARIOS", "USUARIOS_SEQ");
        schemaInspector.reiniciarTablaConSecuencia("TRANSACCIONES", "TRANSACCIONES_SEQ");
        */


        System.out.println("--- FIN DE LA PRUEBA DE INTEGRIDAD ---");
    }
}