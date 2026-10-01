CREATE TABLE ventas (
                        id UUID PRIMARY KEY,
                        empresa_id UUID NOT NULL,
                        cliente_id UUID NOT NULL,
                        numero_documento VARCHAR(50),
                        fecha_venta TIMESTAMP WITH TIME ZONE NOT NULL,
                        estado VARCHAR(20) NOT NULL,
                        total NUMERIC(15,2) NOT NULL,
                        observacion VARCHAR(500),
                        fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                        fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                        CONSTRAINT fk_ventas_empresa
                            FOREIGN KEY (empresa_id) REFERENCES empresas(id),

                        CONSTRAINT fk_ventas_cliente
                            FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE venta_detalles (
                                id UUID PRIMARY KEY,
                                venta_id UUID NOT NULL,
                                producto_id UUID NOT NULL,
                                cantidad NUMERIC(15,3) NOT NULL,
                                precio_unitario NUMERIC(15,2) NOT NULL,
                                subtotal NUMERIC(15,2) NOT NULL,

                                CONSTRAINT fk_venta_detalles_venta
                                    FOREIGN KEY (venta_id) REFERENCES ventas(id),

                                CONSTRAINT fk_venta_detalles_producto
                                    FOREIGN KEY (producto_id) REFERENCES productos(id),

                                CONSTRAINT ck_venta_detalles_cantidad
                                    CHECK (cantidad > 0),

                                CONSTRAINT ck_venta_detalles_precio
                                    CHECK (precio_unitario >= 0)
);

CREATE INDEX idx_ventas_empresa
    ON ventas(empresa_id);

CREATE INDEX idx_ventas_cliente
    ON ventas(cliente_id);

CREATE INDEX idx_venta_detalles_venta
    ON venta_detalles(venta_id);