
CREATE TABLE plantilla_contrato (
                                    id          UUID PRIMARY KEY,
                                    tipo        VARCHAR(20)  NOT NULL,
                                    pais        VARCHAR(2)   NOT NULL,
                                    version     INT          NOT NULL,
                                    cuerpo      TEXT         NOT NULL,
                                    activa      BOOLEAN      NOT NULL DEFAULT TRUE,
                                    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),

                                    CONSTRAINT ck_plantilla_tipo CHECK (tipo IN ('ARRAS', 'ALQUILER', 'COMPRAVENTA')),
                                    CONSTRAINT uk_plantilla_version UNIQUE (tipo, pais, version)
);


CREATE UNIQUE INDEX uk_plantilla_activa
    ON plantilla_contrato (tipo, pais)
    WHERE activa = TRUE;

CREATE TABLE contrato (
                          id               UUID PRIMARY KEY,
                          lead_id          UUID           NOT NULL,
                          propiedad_id     UUID           NOT NULL,
                          cliente_id       UUID           NOT NULL,
                          agente_id        UUID           NOT NULL,
                          plantilla_id     UUID           NOT NULL REFERENCES plantilla_contrato (id),
                          tipo             VARCHAR(20)    NOT NULL,
                          estado           VARCHAR(20)    NOT NULL,
                          pais             VARCHAR(2)     NOT NULL,
                          precio_acordado  NUMERIC(15, 2),
                          contenido        TEXT           NOT NULL,
                          firmado_en       TIMESTAMP,
                          created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
                          updated_at       TIMESTAMP      NOT NULL DEFAULT NOW(),


                          CONSTRAINT uk_contrato_lead UNIQUE (lead_id),
                          CONSTRAINT ck_contrato_tipo   CHECK (tipo   IN ('ARRAS', 'ALQUILER', 'COMPRAVENTA')),
                          CONSTRAINT ck_contrato_estado CHECK (estado IN ('BORRADOR', 'FIRMADO', 'CANCELADO')),
                          CONSTRAINT ck_contrato_precio CHECK (precio_acordado IS NULL OR precio_acordado > 0),

                          CONSTRAINT ck_contrato_firmado CHECK (
                              estado <> 'FIRMADO' OR (firmado_en IS NOT NULL AND precio_acordado IS NOT NULL)
                              )
);

CREATE INDEX idx_contrato_agente_id  ON contrato (agente_id);
CREATE INDEX idx_contrato_cliente_id ON contrato (cliente_id);
CREATE INDEX idx_contrato_propiedad_id ON contrato (propiedad_id);
CREATE INDEX idx_contrato_estado     ON contrato (estado);


INSERT INTO plantilla_contrato (id, tipo, pais, version, cuerpo) VALUES
                                                                     (gen_random_uuid(), 'COMPRAVENTA', 'CO', 1,
                                                                      'CONTRATO DE COMPRAVENTA DE INMUEBLE
                                                                      Fecha: {{fecha}}
                                                                      Vendedor/Representante: {{nombreAgente}}
                                                                      Comprador: {{nombreCliente}}
                                                                      Inmueble: {{direccion}}, {{ciudad}}
                                                                      Precio acordado: {{precio}}
                                                                      Las partes acuerdan la transferencia del inmueble descrito por el precio indicado.'),
                                                                     (gen_random_uuid(), 'ARRAS', 'CO', 1,
                                                                      'CONTRATO DE ARRAS
                                                                      Fecha: {{fecha}}
                                                                      Representante: {{nombreAgente}}
                                                                      Promitente comprador: {{nombreCliente}}
                                                                      Inmueble: {{direccion}}, {{ciudad}}
                                                                      Valor total: {{precio}}
                                                                      El promitente comprador entrega arras como garantía de la compraventa del inmueble descrito.'),
                                                                     (gen_random_uuid(), 'ALQUILER', 'CO', 1,
                                                                      'CONTRATO DE ARRENDAMIENTO DE VIVIENDA
                                                                      Fecha: {{fecha}}
                                                                      Arrendador/Representante: {{nombreAgente}}
                                                                      Arrendatario: {{nombreCliente}}
                                                                      Inmueble: {{direccion}}, {{ciudad}}
                                                                      Canon mensual: {{precio}}
                                                                      El arrendador cede el uso del inmueble descrito al arrendatario por el canon indicado.');