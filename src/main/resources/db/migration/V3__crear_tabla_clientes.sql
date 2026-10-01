CREATE TABLE clientes (
                          id UUID PRIMARY KEY,
                          empresa_id UUID NOT NULL,
                          rut VARCHAR(12),
                          razon_social VARCHAR(150) NOT NULL,
                          email VARCHAR(150),
                          telefono VARCHAR(30),
                          direccion VARCHAR(200),
                          comuna VARCHAR(100),
                          region VARCHAR(100),
                          estado VARCHAR(20) NOT NULL,
                          fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                          fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT fk_clientes_empresa
                              FOREIGN KEY (empresa_id)
                                  REFERENCES empresas(id),

                          CONSTRAINT uk_clientes_empresa_rut
                              UNIQUE (empresa_id, rut)
);