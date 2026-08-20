BEGIN;

CREATE TABLE IF NOT EXISTS inventario.categorias_producto (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE
);

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS es_base BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS tipo_presentacion VARCHAR(20);

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS precio_venta NUMERIC(12,2);

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS producto_origen_id BIGINT;

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS es_residuo BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS codigo VARCHAR(30);

ALTER TABLE inventario.productos
    ADD COLUMN IF NOT EXISTS categoria_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'productos_tipo_presentacion_check'
    ) THEN
        ALTER TABLE inventario.productos
            ADD CONSTRAINT productos_tipo_presentacion_check
            CHECK (tipo_presentacion IN ('FILETE', 'CABEZA', 'POSTA'));
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fkt2ada7evtk80fvphejih26bkj'
    ) THEN
        ALTER TABLE inventario.productos
            ADD CONSTRAINT fkt2ada7evtk80fvphejih26bkj
            FOREIGN KEY (producto_origen_id)
            REFERENCES inventario.productos(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_productos_categoria'
    ) THEN
        ALTER TABLE inventario.productos
            ADD CONSTRAINT fk_productos_categoria
            FOREIGN KEY (categoria_id)
            REFERENCES inventario.categorias_producto(id);
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_productos_codigo
    ON inventario.productos (codigo)
    WHERE codigo IS NOT NULL;

INSERT INTO inventario.categorias_producto (nombre)
VALUES
    ('FILETES DE PESCADO'),
    ('MARISCOS'),
    ('PESCADOS CONGELADOS'),
    ('PESCADOS ENTEROS'),
    ('PESCADOS SIN CABEZA')
ON CONFLICT (nombre) DO NOTHING;

WITH unidad_kg AS (
    SELECT id
    FROM inventario.unidades_medida
    WHERE LOWER(nombre) = 'kg'
    ORDER BY id
    LIMIT 1
),
data(codigo, nombre, categoria) AS (
    VALUES
        ('FB191', 'FILETE DE BASA X 4KG', 'FILETES DE PESCADO'),
        ('FB5XK', 'FILETE DE BASA 5 X KL', 'FILETES DE PESCADO'),
        ('FS111', 'FILETE DE SALMON PREMIUM CONGELADO 2-4 LB', 'FILETES DE PESCADO'),
        ('FT', 'FILETE DE TILAPIA 5-7', 'FILETES DE PESCADO'),
        ('A88', 'ANILLOS DE CALAMAR', 'MARISCOS'),
        ('BC205', 'BOTONES DE CALAMAR', 'MARISCOS'),
        ('CJC201', 'CARNE DE JAIBA', 'MARISCOS'),
        ('CL140', 'COLA DE LANGOSTA 140 AD', 'MARISCOS'),
        ('CM08', 'CAMARON 36-40 CRUDO 30%', 'MARISCOS'),
        ('CM107', 'CAMARON 51-60 CRUDO 30%', 'MARISCOS'),
        ('CM15', 'CAMARON CULTIVO TAIL ON U15 30%', 'MARISCOS'),
        ('CM16/20', 'CAMARON 16/20 PYD 30%', 'MARISCOS'),
        ('CM26/30', 'CAMARON CRUDO 26-30 CRUDO 30%', 'MARISCOS'),
        ('CM30', 'CAMARON CRUDO 21/25 TAIL ON 30%', 'MARISCOS'),
        ('CM31/35', 'CAMARON CRUDO 31-35 30%', 'MARISCOS'),
        ('CM46', 'CAMARON CRUDO 41 - 50 30%', 'MARISCOS'),
        ('G32', 'GAMBA', 'MARISCOS'),
        ('GG250', 'GAMBA GRANDE', 'MARISCOS'),
        ('JP190', 'JAIBA', 'MARISCOS'),
        ('L125', 'LANGOSTINO', 'MARISCOS'),
        ('L16', 'LANGOSTINOS U8', 'MARISCOS'),
        ('L90', 'LANGOSTA ARTESANAL DEL PACIFICO', 'MARISCOS'),
        ('LC68', 'LANGOSTA', 'MARISCOS'),
        ('LG70', 'LANGOSTINO 16-20', 'MARISCOS'),
        ('MM104', 'MIXTURA DE MARISCOS', 'MARISCOS'),
        ('MNC', 'MEJILLON NEGRO CHILENO SIN CONCHA IQF', 'MARISCOS'),
        ('MNP210', 'MEJILLONES NEGROS X 500 GR E/V', 'MARISCOS'),
        ('PC214', 'PIANGUA ARTESANAL', 'MARISCOS'),
        ('PCH23', 'PULPO CHILENO 2 - 4', 'MARISCOS'),
        ('PFG27', 'PULPO PHILIPINO 4-6', 'MARISCOS'),
        ('PLT50', 'PALMITOS DE CANGREJOS X 454 GR', 'MARISCOS'),
        ('PM240', 'PULPO MEXICANO 2-4', 'MARISCOS'),
        ('AC95', 'ALGUACIL', 'PESCADOS CONGELADOS'),
        ('BC206', 'BRAVO', 'PESCADOS CONGELADOS'),
        ('CC203', 'CABEZUDO', 'PESCADOS CONGELADOS'),
        ('CC210', 'CORVINA', 'PESCADOS CONGELADOS'),
        ('5647', 'SIERRA', 'PESCADOS ENTEROS'),
        ('SP10', 'SIERRA PEQUENA', 'PESCADOS ENTEROS'),
        ('ASC138', 'ATUN PATISECA', 'PESCADOS ENTEROS'),
        ('B97', 'BURIQUE', 'PESCADOS ENTEROS'),
        ('BP72', 'BERRUGATE PEQUENO', 'PESCADOS ENTEROS'),
        ('BRR22', 'BERRUGATE', 'PESCADOS ENTEROS'),
        ('BT201', 'BOTELLONA', 'PESCADOS ENTEROS'),
        ('C150', 'CORVINA PEQUENA', 'PESCADOS ENTEROS'),
        ('C87', 'CHERNILLA', 'PESCADOS ENTEROS'),
        ('CH28', 'CHERNA', 'PESCADOS ENTEROS'),
        ('D23', 'DORADO', 'PESCADOS ENTEROS'),
        ('M08', 'MERLUZA S/C 1,5-5 KG', 'PESCADOS ENTEROS'),
        ('M10', 'MACHETAJO', 'PESCADOS ENTEROS'),
        ('M83', 'MERO GRANDE 30-60 KG', 'PESCADOS ENTEROS'),
        ('M85', 'MERLUCILLA', 'PESCADOS ENTEROS'),
        ('ME41', 'MERO ESPECIAL 25-28 KG', 'PESCADOS ENTEROS'),
        ('N33', 'NATO', 'PESCADOS ENTEROS'),
        ('P370', 'PALMA', 'PESCADOS ENTEROS'),
        ('P59', 'PICUDA', 'PESCADOS ENTEROS'),
        ('PC125', 'PESCADILLA', 'PESCADOS ENTEROS'),
        ('PG1211', 'PARGUILLO', 'PESCADOS ENTEROS'),
        ('P3/4', 'PARGO 3/4', 'PESCADOS ENTEROS'),
        ('PP19', 'PARGO PLATERO LUNAREJO', 'PESCADOS ENTEROS'),
        ('PPCC240', 'PARGO PLATERO COLIAMARILLO', 'PESCADOS ENTEROS'),
        ('PPR236', 'PARGO PLATERO ROJO', 'PESCADOS ENTEROS'),
        ('P11', 'PARGO MEDIANO ROQUERO', 'PESCADOS ENTEROS'),
        ('PJ18', 'PARGO MEDIANO JILGUERO', 'PESCADOS ENTEROS'),
        ('PMC53', 'PARGO MEDIANO COLIAMARILLO', 'PESCADOS ENTEROS'),
        ('PMR56', 'PARGO MEDIANO ROJO', 'PESCADOS ENTEROS'),
        ('PRM1016', 'PARGO MEDIANO LUNARERO', 'PESCADOS ENTEROS'),
        ('PA17', 'PARGO MEDIANO ACHOTE', 'PESCADOS ENTEROS'),
        ('PC12', 'PARGO MEDIANO CHILLAO', 'PESCADOS ENTEROS'),
        ('RFA022', 'ROBALO', 'PESCADOS ENTEROS'),
        ('RL128', 'PESCADOS VARIOS (RALLA)', 'PESCADOS ENTEROS'),
        ('T30', 'PESCADOS VARIOS (TOLLO)', 'PESCADOS ENTEROS'),
        ('PS202', 'PESCADO SECO', 'PESCADOS ENTEROS'),
        ('ASC81', 'ATUN ALBACORA', 'PESCADOS SIN CABEZA')
)
INSERT INTO inventario.productos (
    codigo,
    nombre,
    descripcion,
    unidad_medida_id,
    es_base,
    disponible,
    es_residuo,
    categoria_id
)
SELECT
    d.codigo,
    d.nombre,
    CONCAT('Producto de catalogo - ', d.categoria),
    u.id,
    TRUE,
    TRUE,
    FALSE,
    c.id
FROM data d
JOIN inventario.categorias_producto c ON c.nombre = d.categoria
CROSS JOIN unidad_kg u
ON CONFLICT (nombre) DO UPDATE
SET
    codigo = EXCLUDED.codigo,
    categoria_id = EXCLUDED.categoria_id,
    unidad_medida_id = COALESCE(inventario.productos.unidad_medida_id, EXCLUDED.unidad_medida_id),
    descripcion = COALESCE(inventario.productos.descripcion, EXCLUDED.descripcion),
    disponible = COALESCE(inventario.productos.disponible, EXCLUDED.disponible);

COMMIT;
