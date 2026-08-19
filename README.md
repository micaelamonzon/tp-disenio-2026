# DonaTrack 🎁
 
Sistema de gestión y trazabilidad de donaciones para **UTN Solidaria**, la iniciativa de la Subsecretaría de Asuntos Estudiantiles de UTN FRBA. Trabajo Práctico Anual Integrador de la cátedra **Diseño de Sistemas de Información** (2026).
 
> ⚠️ **Estado: en desarrollo.** Al ser un TP anual, el proyecto avanza por entregas incrementales. Hasta el momento están completas las **Entregas 1, 2 y 3**, y actualmente estoy trabajando en la **Entrega 4** (persistencia y maquetado de UI).
 
## Contexto
 
Una organización sin fines de lucro necesita ordenar y dar trazabilidad a las donaciones que recibe: desde que ingresan al depósito hasta que se entregan a una entidad beneficiaria. DonaTrack resuelve eso con una arquitectura distribuida en servicios.
 
## Arquitectura
 
El sistema se compone de los siguientes servicios:
 
- **Servicio de Donaciones**: alta/baja/modificación de personas donantes (humanas o jurídicas), registro y segmentación automática de donaciones por subcategoría, gestión de entidades beneficiarias y sus necesidades (recurrentes/extraordinarias), trazabilidad de estados de una donación, algoritmos de asignación (matchmaking) entidad-donación.
- **Servicio de Logística**: gestión de flota de camiones, planificación de rutas de entrega (integrado con un planificador externo), trazabilidad de entregas, monitoreo de camiones en tiempo real.
- **Servicio de Incentivos**: analítica de donantes, sistema de misiones e insignias, categorías de donante (Colaborador / Sostenedor / Transformador), ranking mensual con difusión automática en redes sociales (flujo low-code).
- **Servicio de Notificaciones**: envío de notificaciones (email, SMS, WhatsApp) ante eventos relevantes del sistema, integrado de forma asincrónica vía cola de mensajes.
- **Servicio de Autenticación**: gestión de tokens de autorización.
- **Frontend (SSR)**: cliente liviano desacoplado que consume las APIs REST de los demás servicios.
## Funcionalidades implementadas por entrega
 
**Entrega 1 — Modelado y dominio inicial**
- Modelo de dominio de Donantes, Donaciones, Entidades Beneficiarias y Necesidades.
- Importación masiva de donantes por CSV (>10.000 filas).
- Primera iteración simulada del Servicio de Notificaciones.
- Bocetos de interfaz de usuario.
**Entrega 2 — Trazabilidad, asignación e incentivos**
- Máquina de estados completa de una donación (En depósito → Asignación realizada → Lista para entregar → En traslado → Entregada / Entrega fallida / Vencida).
- Algoritmos de asignación (compatibilidad semántica y prioridad a sub-atendidos), ejecutados de forma asincrónica en horarios de baja carga.
- Primera iteración del Servicio de Incentivos: métricas de actividad, misiones, insignias y categorías.
- Integración real con medios de notificación (email/SMS/WhatsApp).
- Exposición de las APIs REST de Donaciones e Incentivos.
**Entrega 3 — Logística**
- Planificación de rutas integrada con un componente externo (vía URL de callback, procesamiento en lotes de hasta 100 donaciones).
- Trazabilidad completa de entregas (inicio de ruta, confirmación de recepción, entregas fallidas/replanificación).
- Monitoreo de camiones en tiempo real (GPS o app móvil del conductor).
- Integración entre Servicio de Donaciones y Servicio de Logística.
- Eventos e notificaciones de inicio de ruta, entrega exitosa y entrega no satisfactoria.
**Entrega 4 — En curso 🚧**
- Persistencia de los modelos de cada servicio (relacional para Donaciones/Incentivos/Notificaciones, documental para Logística) usando ORM/ODM.
- Estrategias de desnormalización para optimizar lecturas.
- Maquetado e implementación en HTML/CSS de las interfaces de usuario diseñadas en la Entrega 1.
**Próximas entregas**
- Entrega 5: cliente liviano desacoplado con arquitectura MVC y servicio de autenticación.
- Entrega 6: despliegue en la nube, observabilidad, seguridad, rate limiting, gRPC/GraphQL y (bonus) arquitectura de microservicios con API Gateway.

## Tecnologías

| Tecnología          | Versión       |
|---------------------|---------------|
| Java                | 21            |
| Spring Boot         | 4.0.5         |
| Spring Cloud BOM    | 2025.1.1      |
| Lombok              | 1.18.34       |
| Maven               | 3.9+          |

El BOM de Spring Cloud está declarado en el POM padre para que los módulos puedan incorporar dependencias de Spring Cloud sin especificar versión explícita.

---

## Desarrollo local (Maven)

Todos los comandos se ejecutan desde la **raíz del proyecto**.

### Compilar todos los módulos

```bash
mvn clean install
```

Esto construye `common-lib` primero y luego los servicios que dependen de ella.

### Ejecutar un servicio

```bash
# Servicio de donaciones (puerto 8080)
mvn spring-boot:run -pl donaciones-service

# Servicio de notificaciones (puerto 8081)
mvn spring-boot:run -pl notificaciones-service
```

Maven resuelve `common-lib` directamente desde el reactor, por lo que no hace falta instalarla por separado si se ejecuta desde la raíz.

---

## Construcción de imágenes Docker

Este proyecto utiliza una arquitectura multi-módulo de Maven. Los microservicios dependen del `pom.xml` padre y de `common-lib`, por lo que **el contexto de construcción de Docker siempre debe ser la raíz del proyecto**. Si se limita el contexto a la carpeta del microservicio, Maven fallará al no encontrar el POM padre ni las dependencias comunes.

### Construcción manual (CLI)

Posicionarse en la carpeta raíz del proyecto y pasar el Dockerfile con `-f`, dejando `.` como contexto:

```bash
# donaciones-service (expone el puerto 8080)
docker build -t donaciones-img -f donaciones-service/Dockerfile .

# notificaciones-service (expone el puerto 8081)
docker build -t notificaciones-img -f notificaciones-service/Dockerfile .
```

### Ejecutar los contenedores

```bash
docker run -p 8080:8080 donaciones-img
docker run -p 8081:8081 notificaciones-img
```

### Nota sobre `ARG SERVICE_NAME`

Cada Dockerfile define un `ARG SERVICE_NAME` cuyo valor por defecto ya coincide con el nombre del servicio (p. ej. `donaciones-service`). Solo es necesario sobreescribirlo si se reutiliza un Dockerfile genérico para construir un servicio diferente:

```bash
docker build --build-arg SERVICE_NAME=otro-service -f otro-service/Dockerfile .
```

---

## Estado del proyecto

Los servicios son aplicaciones Spring Boot mínimas, listas para extender con controladores, repositorios y lógica de negocio. `common-lib` contiene el código compartido entre servicios.
