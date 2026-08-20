-- @param fecha_inicio|date|Fecha inicio|false
-- @param fecha_fin|date|Fecha fin|false
-- @column id|ID|number
-- @column fecha|Fecha|date
-- @column cod_cargue|Cod cargue|text
-- @column proveedor|Proveedor|text
-- @column producto|Producto base|text
-- @column cantidad_total|Cantidad total|number
-- @column total_compra|Total compra|currency
SELECT
  c.id,
  c.fecha,
  c.cod_cargue,
  COALESCE(pv.nombre, 'Sin proveedor') AS proveedor,
  p.nombre AS producto,
  c.cantidad_total,
  (c.cantidad_total * c.valor_unitario_compra) AS total_compra
FROM compras c
JOIN productos p ON p.id = c.producto_base_id
LEFT JOIN proveedores pv ON pv.id = c.proveedor_id
WHERE (:fecha_inicio IS NULL OR c.fecha::date >= CAST(:fecha_inicio AS DATE))
  AND (:fecha_fin IS NULL OR c.fecha::date <= CAST(:fecha_fin AS DATE))
ORDER BY c.fecha DESC;