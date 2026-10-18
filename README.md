# Realty Contract

Realty Contract es el microservicio encargado de gestionar la información relacionada con los contratos inmobiliarios de RealtyHub. Su propósito es centralizar los registros contractuales asociados a las operaciones inmobiliarias y facilitar su administración dentro del sistema.

## Funcionalidades

- Generación de contratos (arras, alquiler y compraventa) a partir de plantillas.
- Firma y cancelación de contratos.
- Generación del documento PDF del contrato, almacenado en Cloudflare R2.
- Publica eventos cuando un contrato se firma o se cancela.

## Tecnologías

Java 21, Spring Boot, PostgreSQL, Flyway, RabbitMQ, OpenPDF y Cloudflare R2.

## Puerto

El servicio se ejecuta en el puerto `8086`.
