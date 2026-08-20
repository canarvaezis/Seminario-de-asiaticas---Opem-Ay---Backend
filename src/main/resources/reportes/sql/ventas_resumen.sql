-- @param fecha_inicio|date|Fecha inicio|false
-- @param fecha_fin|date|Fecha fin|false
-- @column id|ID|number
-- @column fecha|Fecha|date
-- @column comprador|Comprador|text
-- @column total|Total|currency
-- @column items|Items|number
SELECT
  v.id,
  v.fecha,
  COALESCE(c.nombre, 'Sin comprador') AS comprador,
  v.total,
  COALESCE(COUNT(dv.id), 0) AS items
FROM ventas v
LEFT JOIN compradores c ON c.id = v.comprador_id
LEFT JOIN detalle_venta dv ON dv.venta_id = v.id
WHERE (:fecha_inicio IS NULL OR v.fecha::date >= CAST(:fecha_inicio AS DATE))
  AND (:fecha_fin IS NULL OR v.fecha::date <= CAST(:fecha_fin AS DATE))
GROUP BY v.id, v.fecha, c.nombre, v.total
ORDER BY v.fecha DESC;