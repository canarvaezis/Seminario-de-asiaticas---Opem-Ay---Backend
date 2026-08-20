-- Script SQL para la base de datos de Pescadería
-- PostgreSQL

-- Crear base de datos
CREATE DATABASE pescaderia_db;

\c pescaderia_db;

-- Roles
CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (nombre) VALUES
('administrador'),
('contador'),
('aux_contable');

-- Usuarios
CREATE TABLE usuarios (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100),
  correo VARCHAR(150) UNIQUE,
  contrasena VARCHAR(200) NOT NULL,
  rol_id INT NOT NULL REFERENCES roles(id)
);

-- Unidades de medida
CREATE TABLE unidades_medida (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(20) UNIQUE NOT NULL
);

INSERT INTO unidades_medida (nombre) VALUES
('kg'),
('unidad');

-- Productos
CREATE TABLE productos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(150) UNIQUE NOT NULL,
  descripcion VARCHAR(500),
  unidad_medida_id INT NOT NULL REFERENCES unidades_medida(id),
  es_base BOOLEAN NOT NULL DEFAULT FALSE,
  tipo_presentacion VARCHAR(20) CHECK (tipo_presentacion IN ('FILETE','CABEZA','POSTA')),
  precio_venta NUMERIC(12,2),
  disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- Productos derivados del pez (se asignan con tipo_presentacion para el cálculo automático)
-- El administrador puede ajustar los nombres desde el módulo de productos.
INSERT INTO productos (nombre, unidad_medida_id, es_base, tipo_presentacion, disponible) VALUES
  ('Filete', 1, FALSE, 'FILETE', TRUE),
  ('Cabeza', 1, FALSE, 'CABEZA', TRUE),
  ('Posta',  1, FALSE, 'POSTA',  TRUE)
ON CONFLICT (nombre) DO NOTHING;

-- Compras
CREATE TABLE compras (
  id SERIAL PRIMARY KEY,
  cod_cargue VARCHAR(50) UNIQUE NOT NULL,
  usuario_id INT NOT NULL REFERENCES usuarios(id),
  producto_base_id INT NOT NULL REFERENCES productos(id),
  unidad_medida_id INT NOT NULL REFERENCES unidades_medida(id),
  cantidad_total NUMERIC(12,3) NOT NULL CHECK (cantidad_total > 0),
  valor_unitario_compra NUMERIC(12,2) NOT NULL,
  fecha TIMESTAMP DEFAULT now(),
  cantidad_disponible_sin_trabajar NUMERIC(12,3) NOT NULL
);

-- Trigger para inicializar stock
CREATE OR REPLACE FUNCTION inicializar_stock_compra()
RETURNS TRIGGER AS $$
BEGIN
  NEW.cantidad_disponible_sin_trabajar := NEW.cantidad_total;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_inicializar_stock
BEFORE INSERT ON compras
FOR EACH ROW
EXECUTE FUNCTION inicializar_stock_compra();

-- Preparaciones
CREATE TABLE preparaciones (
  id SERIAL PRIMARY KEY,
  compra_id INT NOT NULL REFERENCES compras(id),
  usuario_id INT NOT NULL REFERENCES usuarios(id),
  fecha TIMESTAMP DEFAULT now(),
  cantidad_entrada NUMERIC(12,3) NOT NULL CHECK (cantidad_entrada > 0),
  tipo_procedimiento VARCHAR(20) NOT NULL CHECK (tipo_procedimiento IN ('DIRECTO','FILETE_CABEZA','POSTA')),
  cantidad_basura NUMERIC(12,3) NOT NULL DEFAULT 0
);

-- Detalle de preparación
CREATE TABLE detalle_preparacion (
  id SERIAL PRIMARY KEY,
  preparacion_id INT NOT NULL REFERENCES preparaciones(id) ON DELETE CASCADE,
  producto_resultado_id INT NOT NULL REFERENCES productos(id),
  cantidad_salida NUMERIC(12,3) NOT NULL CHECK (cantidad_salida >= 0)
);

-- Stock trabajado
CREATE TABLE stock_trabajado (
  compra_id INT NOT NULL REFERENCES compras(id) ON DELETE CASCADE,
  producto_id INT NOT NULL REFERENCES productos(id),
  cantidad NUMERIC(12,3) NOT NULL DEFAULT 0,
  PRIMARY KEY (compra_id, producto_id)
);

-- Trigger para descontar sin trabajar
CREATE OR REPLACE FUNCTION descontar_sin_trabajar()
RETURNS TRIGGER AS $$
DECLARE
  disponible NUMERIC;
BEGIN
  SELECT cantidad_disponible_sin_trabajar
  INTO disponible
  FROM compras
  WHERE id = NEW.compra_id
  FOR UPDATE;

  IF disponible < NEW.cantidad_entrada THEN
    RAISE EXCEPTION 'No hay cantidad suficiente sin trabajar';
  END IF;

  UPDATE compras
  SET cantidad_disponible_sin_trabajar =
      cantidad_disponible_sin_trabajar - NEW.cantidad_entrada
  WHERE id = NEW.compra_id;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_preparacion_descuento
AFTER INSERT ON preparaciones
FOR EACH ROW
EXECUTE FUNCTION descontar_sin_trabajar();

-- Trigger para sumar stock trabajado
CREATE OR REPLACE FUNCTION sumar_stock_trabajado()
RETURNS TRIGGER AS $$
DECLARE
  compraId INT;
BEGIN
  SELECT compra_id INTO compraId
  FROM preparaciones
  WHERE id = NEW.preparacion_id;

  INSERT INTO stock_trabajado (compra_id, producto_id, cantidad)
  VALUES (compraId, NEW.producto_resultado_id, NEW.cantidad_salida)
  ON CONFLICT (compra_id, producto_id)
  DO UPDATE SET cantidad =
    stock_trabajado.cantidad + EXCLUDED.cantidad;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sumar_stock
AFTER INSERT ON detalle_preparacion
FOR EACH ROW
EXECUTE FUNCTION sumar_stock_trabajado();

-- Ventas
CREATE TABLE ventas (
  id SERIAL PRIMARY KEY,
  usuario_id INT NOT NULL REFERENCES usuarios(id),
  fecha TIMESTAMP DEFAULT now(),
  total NUMERIC(14,2) DEFAULT 0
);

-- Detalle de venta
CREATE TABLE detalle_venta (
  id SERIAL PRIMARY KEY,
  venta_id INT NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
  compra_id INT NOT NULL REFERENCES compras(id),
  producto_id INT NOT NULL REFERENCES productos(id),
  cantidad NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario NUMERIC(12,2) NOT NULL,
  subtotal NUMERIC(14,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED
);

-- Trigger para descontar stock en venta
CREATE OR REPLACE FUNCTION descontar_stock_venta()
RETURNS TRIGGER AS $$
DECLARE
  esBase BOOLEAN;
  disponible NUMERIC;
BEGIN
  SELECT es_base INTO esBase
  FROM productos
  WHERE id = NEW.producto_id;

  IF esBase THEN
    SELECT cantidad_disponible_sin_trabajar
    INTO disponible
    FROM compras
    WHERE id = NEW.compra_id
    FOR UPDATE;

    IF disponible < NEW.cantidad THEN
      RAISE EXCEPTION 'No hay stock sin trabajar suficiente';
    END IF;

    UPDATE compras
    SET cantidad_disponible_sin_trabajar =
        cantidad_disponible_sin_trabajar - NEW.cantidad
    WHERE id = NEW.compra_id;

  ELSE
    SELECT cantidad INTO disponible
    FROM stock_trabajado
    WHERE compra_id = NEW.compra_id
      AND producto_id = NEW.producto_id
    FOR UPDATE;

    IF disponible IS NULL OR disponible < NEW.cantidad THEN
      RAISE EXCEPTION 'No hay stock trabajado suficiente';
    END IF;

    UPDATE stock_trabajado
    SET cantidad = cantidad - NEW.cantidad
    WHERE compra_id = NEW.compra_id
      AND producto_id = NEW.producto_id;
  END IF;

  UPDATE ventas
  SET total = (
    SELECT COALESCE(SUM(subtotal),0)
    FROM detalle_venta
    WHERE venta_id = NEW.venta_id
  )
  WHERE id = NEW.venta_id;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_descuento_venta
AFTER INSERT ON detalle_venta
FOR EACH ROW
EXECUTE FUNCTION descontar_stock_venta();

-- Vista de inventario general
CREATE VIEW inventario_general AS
SELECT
  c.id AS compra_id,
  c.cod_cargue,
  p.nombre AS producto_base,
  c.cantidad_total,
  c.cantidad_disponible_sin_trabajar
FROM compras c
JOIN productos p ON p.id = c.producto_base_id;
