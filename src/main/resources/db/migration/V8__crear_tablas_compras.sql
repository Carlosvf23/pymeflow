CREATE TABLE compras (
                         id UUID PRIMARY KEY,
                         empresa_id UUID NOT NULL,
                         proveedor_id UUID NOT NULL,
                         numero_documento VARCHAR(50),
                         fecha_compra TIMESTAMP WITH TIME ZONE NOT NULL,
                         estado VARCHAR(20) NOT NULL,
                         total NUMERIC(15,2) NOT NULL,
                         observacion VARCHAR(500),
                         fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                         fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                         CONSTRAINT fk_compras_empresa
                             FOREIGN KEY (empresa_id) REFERENCES empresas(id),

                         CONSTRAINT fk_compras_proveedor
                             FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
);

CREATE TABLE compra_detalles (
                                 id UUID PRIMARY KEY,
                                 compra_id UUID NOT NULL,
                                 producto_id UUID NOT NULL,
                                 cantidad NUMERIC(15,3) NOT NULL,
                                 precio_unitario NUMERIC(15,2) NOT NULL,
                                 subtotal NUMERIC(15,2) NOT NULL,

                                 CONSTRAINT fk_compra_detalles_compra
                                     FOREIGN KEY (compra_id) REFERENCES compras(id),

                                 CONSTRAINT fk_compra_detalles_producto
                                     FOREIGN KEY (producto_id) REFERENCES productos(id),

                                 CONSTRAINT ck_compra_detalles_cantidad
                                     CHECK (cantidad > 0),

                                 CONSTRAINT ck_compra_detalles_precio
                                     CHECK (precio_unitario >= 0)
);

CREATE INDEX idx_compras_empresa
    ON compras(empresa_id);

CREATE INDEX idx_compras_proveedor
    ON compras(proveedor_id);

CREATE INDEX idx_compra_detalles_compra
    ON compra_detalles(compra_id);