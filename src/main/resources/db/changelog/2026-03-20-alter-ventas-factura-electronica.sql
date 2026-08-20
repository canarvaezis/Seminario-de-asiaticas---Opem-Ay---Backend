-- Ajustes para soporte de factura electronica y campos extendidos de ventas.
-- Script idempotente para bases existentes.

ALTER TABLE inventario.ventas
    ADD COLUMN IF NOT EXISTS comprador_id BIGINT REFERENCES inventario.compradores(id),
    ADD COLUMN IF NOT EXISTS subtotal_productos NUMERIC(14,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS costo_envio_por_kg NUMERIC(12,2) DEFAULT 1500,
    ADD COLUMN IF NOT EXISTS costo_envio_total NUMERIC(14,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS factura_electronica_generada BOOLEAN DEFAULT false,
    ADD COLUMN IF NOT EXISTS factura_electronica_numero VARCHAR(40),
    ADD COLUMN IF NOT EXISTS factura_electronica_cufe VARCHAR(64),
    ADD COLUMN IF NOT EXISTS factura_electronica_estado VARCHAR(30),
    ADD COLUMN IF NOT EXISTS factura_electronica_url_pdf VARCHAR(255),
    ADD COLUMN IF NOT EXISTS factura_electronica_fecha_emision TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_ventas_factura_numero
    ON inventario.ventas (factura_electronica_numero);

CREATE INDEX IF NOT EXISTS idx_ventas_factura_cufe
    ON inventario.ventas (factura_electronica_cufe);
