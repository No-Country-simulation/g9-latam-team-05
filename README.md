# Rama de Infraestructura y Despliegue CDN

> **Nota de Gobernanza:** Esta rama (`test/infra-setup`) se mantiene como un entorno aislado de pruebas para la arquitectura asíncrona (Vercel CDN + OCI). No requiere integración (`merge`) con `main`, la cual concentra la versión de producción sincrónica del proyecto.

## Stack y Decisiones de Diseño
* **Frontend CDN:** Angular alojado en Vercel CDN con pipeline de CI/CD activo.
* **Procesamiento Asíncrono:** Arquitectura basada en eventos para respuesta no bloqueante en la interfaz de usuario.
* **Infraestructura Cloud:** 2 instancias OCI `VM.Standard.E2.1.Micro` (Spring Boot Java & FastAPI Python).
* **Persistencia:** Oracle Autonomous Database (ATP) mediante conexión mTLS.

## Topología de Red y Comunicación Inter-VM (OCI)

Para garantizar la seguridad de los microservicios, el backend y el motor de IA se comunican mediante una red privada virtual dentro de OCI:

* **Backend Gateway (Java Spring Boot):** 
  * Expone endpoints HTTP/HTTPS seguros hacia el cliente (Vercel CDN) mediante IP pública.
* **Microservicio IA (Python FastAPI):** 
  * Alojado en una instancia dedicada sin puerto de IA público.
* **Canal de Comunicación Privado:** 
  * Spring Boot consume las inferencias de FastAPI a través de la **IP privada interna** (VCN de OCI) en el puerto local `:8000`.
  * **Reglas de Ingress (Security List):** El acceso al puerto `:8000` está restringido exclusivamente a las peticiones provenientes de la IP privada de la VM de Java.

## Sincronización Automática en Frontend (Polling Optimista)

Para complementar la naturaleza no bloqueante del backend asíncrono, se implementó un mecanismo de actualización continua en el cliente Angular (`transaction.ts`):

* **Persistencia Inmediata:** La transacción se guarda al instante en la UI con estado "sin categorizar".
* **Polling Temporizado:** El servicio consulta periódicamente al backend en segundo plano sin bloquear la navegación del usuario.
* **Re-renderizado Dinámico:** Una vez que el microservicio de IA procesa la inferencia en OCI y la persiste en Oracle ATP, la interfaz detecta el cambio y actualiza automáticamente la categoría en pantalla sin requerir una recarga manual (`F5`).