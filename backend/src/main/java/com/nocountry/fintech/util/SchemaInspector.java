package com.nocountry.fintech.util;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.sql.Statement;

@Component
public class SchemaInspector {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @SuppressWarnings("unchecked")
    public void listarTablasDelEsquema() {
        try {
            List<String> tablas = entityManager.createNativeQuery(
                "SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME ASC"
            ).getResultList();

            System.out.println("--- TABLAS EN EL ESQUEMA: APP_SPRING_BOOT ---");
            
            if (tablas.isEmpty()) {
                System.out.println("No se encontraron tablas en este esquema.");
            } else {
                tablas.forEach(tabla -> System.out.println("Tabla: " + tabla));
            }
        } catch (Exception e) {
            System.err.println("Error al listar las tablas: " + e.getMessage());
        }
    }

    public void verificarConexionOracle() {
        try (Connection connection = dataSource.getConnection()) {
            String dbName = connection.getMetaData().getDatabaseProductName();
            String dbVersion = connection.getMetaData().getDatabaseProductVersion();
            System.out.println("Conexion con oracle database establecida (" + dbName + " " + dbVersion + ")");
        } catch (Exception e) {
            System.err.println("No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    
    //Lista las columnas de una tabla específica
    public void listarColumnasDeTabla(String nombreTabla) {

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            String tablaBusqueda = nombreTabla.toUpperCase();
            
            try (ResultSet rs = metaData.getColumns(null, null, tablaBusqueda, null)) {
                
                System.out.println("--- COLUMNAS DE LA TABLA: " + tablaBusqueda + " ---");
                boolean tieneColumnas = false;
                
                while (rs.next()) {
                    String nombreColumna = rs.getString("COLUMN_NAME");
                    String tipoDato = rs.getString("TYPE_NAME");
                    int tamanio = rs.getInt("COLUMN_SIZE");
                    String nullable = rs.getString("IS_NULLABLE");
                    
                    System.out.println(String.format("Columna: %-20s | Tipo: %-15s | Tamaño: %-5d | ¿Permite Nulos?: %s",
                            nombreColumna, tipoDato, tamanio, nullable));
                    tieneColumnas = true;
                }
                
                if (!tieneColumnas) {
                    System.out.println("No se encontraron columnas o la tabla no existe en este esquema.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los metadatos de la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> obtenerNombresDeTablas() {
        try {
            return entityManager.createNativeQuery(
                "SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME ASC"
            ).getResultList();
        } catch (Exception e) {
            System.err.println("Error al consultar las tablas: " + e.getMessage());
            return List.of();
        }
    }

    public void listarColumnasDeTodasLasTablas() {
        List<String> tablas = obtenerNombresDeTablas();

        for (String tabla : tablas) {
            listarColumnasDeTabla(tabla);
        }
    }
    
    // Mostrar datos de tabla
    public void listarDatosDeTabla(String nombreTabla) {
        String sql = "SELECT * FROM " + nombreTabla.toUpperCase();
        
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnasCount = metaData.getColumnCount();

            System.out.println("\n--- DATOS DE LA TABLA: " + nombreTabla.toUpperCase() + " ---");
            
            // Imprimir cabeceras de columnas
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= columnasCount; i++) {
                header.append(metaData.getColumnName(i)).append("\t| ");
            }
            System.out.println(header.toString());
            System.out.println("-".repeat(header.length()));

            // Imprimir filas
            int contadorFilas = 0;
            while (resultSet.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnasCount; i++) {
                    Object valor = resultSet.getObject(i);
                    row.append(valor != null ? valor.toString() : "NULL").append("\t| ");
                }
                System.out.println(row.toString());
                contadorFilas++;
            }
            
            if (contadorFilas == 0) {
                System.out.println("(La tabla está vacía)");
            } else {
                System.out.println("Total de registros encontrados: " + contadorFilas);
            }
            System.out.println("--------------------------------------------------n\n");

        } catch (Exception e) {
            System.err.println("Error al consultar los datos de la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }

    // Borrar registros de tabla
    public void limpiarTabla(String nombreTabla) {
        String sql = "DELETE FROM " + nombreTabla.toUpperCase();
        
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            int filasAfectadas = statement.executeUpdate(sql);
            System.out.println("--- Se han eliminado " + filasAfectadas + " registros de la tabla: " + nombreTabla.toUpperCase() + " ---");

        } catch (Exception e) {
            System.err.println("Error al intentar limpiar la tabla " + nombreTabla + ": " + e.getMessage());
        }
    }

    public void reiniciarTablaConSecuencia(String nombreTabla, String nombreSecuencia) {
        String sqlDelete = "DELETE FROM " + nombreTabla.toUpperCase();
        String sqlResetSeq = "ALTER SEQUENCE " + nombreSecuencia.toUpperCase() + " RESTART START WITH 1";

        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            // 1. Borrar los registros
            int filasAfectadas = statement.executeUpdate(sqlDelete);
            System.out.println("--- Se han eliminado " + filasAfectadas + " registros de la tabla: " + nombreTabla.toUpperCase() + " ---");

            // 2. Reiniciar la secuencia
            statement.execute(sqlResetSeq);
            System.out.println("--- Secuencia " + nombreSecuencia.toUpperCase() + " reiniciada a 1 ---");

        } catch (Exception e) {
            System.err.println("Error al limpiar la tabla o reiniciar la secuencia " + nombreTabla + ": " + e.getMessage());
        }
    }



}