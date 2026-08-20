-- @column compra_id|Compra|number
-- @column cod_cargue|Cod cargue|text
-- @column producto_nombre|Producto|text
-- @column cantidad|Cantidad disponible|number
SELECT
  st.compra_id,
  c.cod_cargue,
  p.nombre AS producto_nombre,
  st.cantidad
FROM stock_trabajado st
JOIN compras c ON c.id = st.compra_id
JOIN productos p ON p.id = st.producto_id
WHERE st.cantidad > 0
ORDER BY st.cantidad DESC;