CREATE TABLE movimientos_inventario (
                                        id UUID PRIMARY KEY,
                                        empresa_id UUID NOT NULL,
                                        producto_id UUID NOT NULL,
                                        tipo VARCHAR(30) NOT NULL,
                                        cantidad NUMERIC(15,3) NOT NULL,
                                        observacion VARCHAR(500),
                                        fecha_movimiento TIMESTAMP WITH TIME ZONE NOT NULL,
                                        fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,

                                        CONSTRAINT fk_movimientos_inventario_empresa
                                            FOREIGN KEY (empresa_id) REFERENCES empresas(id),

                                        CONSTRAINT fk_movimientos_inventario_producto
                                            FOREIGN KEY (producto_id) REFERENCES productos(id),

                                        CONSTRAINT ck_movimientos_inventario_cantidad
                                            CHECK (cantidad > 0)
);

CREATE INDEX idx_movimientos_inventario_empresa_producto
    ON movimientos_inventario(empresa_id, producto_id);

CREATE INDEX idx_movimientos_inventario_fecha
    ON movimientos_inventario(fecha_movimiento);