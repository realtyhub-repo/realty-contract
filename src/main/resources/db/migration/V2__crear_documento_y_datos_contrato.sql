ALTER TABLE contrato
    ADD COLUMN nombre_cliente      VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN nombre_agente       VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN direccion_propiedad VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN ciudad_propiedad    VARCHAR(120) NOT NULL DEFAULT '';

CREATE TABLE documento (
    id             UUID PRIMARY KEY,
    contrato_id    UUID         NOT NULL REFERENCES contrato (id),
    version        INT          NOT NULL,
    clave_archivo  VARCHAR(255) NOT NULL,
    hash_sha256    VARCHAR(64)  NOT NULL,
    tamano_bytes   BIGINT       NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_documento_contrato_version UNIQUE (contrato_id, version),
    CONSTRAINT ck_documento_version CHECK (version > 0)
);

CREATE INDEX idx_documento_contrato_id ON documento (contrato_id);
