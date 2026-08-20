-- Tablas para reportes dinamicos y permisos por usuario
CREATE TABLE IF NOT EXISTS inventario.reportes (
  id BIGSERIAL PRIMARY KEY,
  codigo VARCHAR(80) NOT NULL UNIQUE,
  nombre VARCHAR(150) NOT NULL,
  descripcion VARCHAR(500),
  archivo_sql VARCHAR(200) NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS inventario.usuario_reporte_permisos (
  id BIGSERIAL PRIMARY KEY,
  usuario_id BIGINT NOT NULL REFERENCES inventario.usuarios(id),
  reporte_id BIGINT NOT NULL REFERENCES inventario.reportes(id),
  puede_ver BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_usuario_reporte UNIQUE (usuario_id, reporte_id)
);
