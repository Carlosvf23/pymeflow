CREATE TABLE productos (
                           id UUID PRIMARY KEY,
                           empresa_id UUID NOT NULL,
                           categoria_id UUID NOT NULL,
                           sku VARCHAR(50) NOT NULL,
                           nombre VARCHAR(150) NOT NULL,
                           descripcion VARCHAR(500),
                           precio_compra NUMERIC(15,2) NOT NULL,
                           precio_venta NUMERIC(15,2) NOT NULL,
                           unidad_medida VARCHAR(30) NOT NULL,
                           estado VARCHAR(20) NOT NULL,
                           fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                           fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                           CONSTRAINT fk_productos_empresa
                               FOREIGN KEY (empresa_id)
                                   REFERENCES empresas(id),

                           CONSTRAINT fk_productos_categoria
                               FOREIGN KEY (categoria_id)
                                   REFERENCES categorias(id),

                           CONSTRAINT uk_productos_empresa_sku
                               UNIQUE (empresa_id, sku)
);