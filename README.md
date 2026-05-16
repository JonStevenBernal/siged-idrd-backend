# SIGED-IDRD Backend

Backend Spring Boot + GraphQL.

## Orden funcional aplicado desde SRS

1. RF-01 / HU-001: crear y publicar torneos con fechas, categorias, formato, reglas y reglamento PDF.
2. RF-02 / HU-002: inscribir equipos y jugadores en torneos abiertos, bloqueando cedulas duplicadas por torneo.
3. RF-03 / HU-003: registrar marcadores y eventos de partido.
4. RF-04 / RF-05: generar fixture basico y calcular tabla de posiciones en tiempo real.
5. RF-06 / HU-004: crear usuarios, asignar roles e inactivar cuentas con MongoDB.

## Comandos

```powershell
docker compose up -d
mvn spring-boot:run
```

Puertos por defecto:

```text
PostgreSQL: localhost:5432
MongoDB: localhost:27017
Backend: localhost:8080
```

GraphiQL:

```text
http://localhost:8080/graphiql
```
