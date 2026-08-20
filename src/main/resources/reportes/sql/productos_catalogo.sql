-- @param solo_disponibles|boolean|Solo disponibles|false
-- @column id|ID|number
-- @column codigo|Codigo|text
-- @column nombre|Nombre|text
-- @column tipo_presentacion|Tipo|text
-- @column precio_venta|Precio|currency
-- @column disponible|Disponible|status
SELECT
  p.id,
  p.codigo,
  p.nombre,
  COALESCE(p.tipo_presentacion, 'BASE/GENERICO') AS tipo_presentacion,
  COALESCE(p.precio_venta, 0) AS precio_venta,
  CASE WHEN p.disponible THEN 'SI' ELSE 'NO' END AS disponible
FROM productos p
WHERE (COALESCE(CAST(:solo_disponibles AS boolean), false) = false OR p.disponible = true)
ORDER BY p.nombre ASC;