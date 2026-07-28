# ☕ Finance AI - Java Spring Boot Backend

Este es el Backend Principal y API Gateway Orquestador del sistema **Finance AI**, desarrollado con **Java 21 LTS** y **Spring Boot 3.4.1**.

---

## ⚠️ NOTA CRÍTICA DE VERSIÓN DE JAVA (PROBLEMA CON JDK 26 / PREVIEW)

> [!CAUTION]
> **IMPORTANTE - INCOMPATIBILIDAD CON JDK 26 / COMPILER PREVIEWS:**
> Al inicio del proyecto se detectaron fallos de compilación e incompatibilidad de bytecode (`class file version 70.0`, `java.lang.IllegalArgumentException` en ByteBuddy / Hibernate / Lombok) al intentar compilar el proyecto usando versiones experimentales no LTS como **JDK 26**.
> 
> **REQUISITO OBLIGATORIO:**
> Debes usar estricta y únicamente **JAVA 21 LTS** (o Java 17 LTS). Spring Boot 3.4 y Hibernate ORM no soportan bytecode experimental de versiones preview superiores sin compatibilidad probada.

---

## 🛠️ Requisitos del Sistema

1. **Java Development Kit (JDK):** Java 21 LTS (OpenJDK 21 u Oracle JDK 21).
2. **Apache Maven:** Versión 3.8+ (incluido o via `mvn`).
3. **Base de Datos Relacional:** PostgreSQL 15+ (Local) u Oracle Autonomous Database (OCI Cloud).

---

## 🗄️ Configuración de Base de Datos Local (PostgreSQL)

El archivo de propiedades configurado por defecto para el desarrollo local se encuentra en:
`src/main/resources/application-postgres.properties`

### 🔑 Credenciales Locales Estándar:
* **URL:** `jdbc:postgresql://localhost:5432/finance_db`
* **Base de Datos:** `finance_db`
* **Usuario:** `postgres`
* **Contraseña:** `${DB_PASSWORD:postgres}` (o la clave asignada en tu PostgreSQL local)
* **Estrategia DDL:** `spring.jpa.hibernate.ddl-auto=update` *(Crea y actualiza automáticamente las 7 tablas del esquema relacional en 3NF)*.

---

## 🚀 Pasos para Iniciar la Aplicación

### 1️⃣ Verificar la Versión de Java
Asegúrate de que la variable de entorno `JAVA_HOME` apunte a Java 21 LTS:
```bash
java -version
```
*Salida esperada:* `openjdk version "21.x.x"` u `java version "21.x.x"`.

### 2️⃣ Crear la Base de Datos en PostgreSQL (Solo la primera vez)
Abre psql o pgAdmin y ejecuta:
```sql
CREATE DATABASE finance_db;
```

### 3️⃣ Compilar el Proyecto Maven
Compila y descarga las dependencias del proyecto:
```bash
mvn clean compile
```

Si deseas empaquetar omitiendo pruebas unitarias prematuras de BD:
```bash
mvn clean package -DskipTests
```

### 4️⃣ Ejecutar el Servidor Backend Spring Boot
Levanta la aplicación en el puerto **8080**:
```bash
mvn exec:exec
```
o alternativamente:
```bash
mvn spring-boot:run
```

---

