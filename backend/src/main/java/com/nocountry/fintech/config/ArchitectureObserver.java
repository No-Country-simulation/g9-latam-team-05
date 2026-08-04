package com.nocountry.fintech.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ArchitectureObserver implements CommandLineRunner {

    @Override
    public void run(String @NonNull ... args) throws Exception {
        System.out.println("\n====================================================================================================");
        System.out.println("📢 OBSERVACIONES DE ARQUITECTURA Y METODOLOGÍA SDLC (FINANCE AI - SEMANA 1)");
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("1. ENTORNO LOCAL: Configurado para PostgreSQL Local (Perfil 'postgres' activo).");
        System.out.println("2. ABSTRACCIÓN JPA: Se desactivaron consultas nativas de Oracle ('USER_TABLES') en SchemaInspector");
        System.out.println("   para cumplir con el estándar de portabilidad relacional JPA (ISO/IEC/IEEE 12207).");
        System.out.println("3. COMPATIBILIDAD DE BUILD: Proyecto configurado en Java 21 LTS y Spring Boot 3.3.5.");
        System.out.println("4. SEPARACIÓN DE INCUMBENCIAS: Las pruebas de integración deben ubicarse aisladas en 'src/test/java'");
        System.out.println("   y las configuraciones de Oracle Cloud OCI se activarán únicamente en la Fase 5 (Despliegue).");
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("📌 RUTA PARA QUITAR O EDITAR ESTE MENSAJE DE CONSOLA:");
        System.out.println("   Elimina o comenta la clase en: src/main/java/com/nocountry/fintech/config/ArchitectureObserver.java");
        System.out.println("====================================================================================================\n");
    }
}
