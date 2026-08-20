-- @param fecha_inicio|date|Fecha inicio|false
-- @param fecha_fin|date|Fecha fin|false
-- @param estado|text|Estado pedido|false
-- @column id|ID|number
-- @column fecha|Fecha|date
-- @column cliente|Cliente|text
-- @column estado|Estado|status
-- @column total|Total|currency
-- @column direccion_entrega|Direccion entrega|text
SELECT
  p.id,
  p.fecha,
  p.cliente_nombre AS cliente,
  p.estado,
  p.total,
  p.direccion_entrega
FROM pedidos p
WHERE (:fecha_inicio IS NULL OR p.fecha::date >= CAST(:fecha_inicio AS DATE))
  AND (:fecha_fin IS NULL OR p.fecha::date <= CAST(:fecha_fin AS DATE))
  AND (:estado IS NULL OR :estado = '' OR p.estado = :estado)
ORDER BY p.fecha DESC;