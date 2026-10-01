CREATE TABLE proveedores (
                             id UUID PRIMARY KEY,
                             empresa_id UUID NOT NULL,
                             rut VARCHAR(12),
                             razon_social VARCHAR(150) NOT NULL,
                             contacto VARCHAR(150),
                             email VARCHAR(150),
                             telefono VARCHAR(30),
                             direccion VARCHAR(200),
                             comuna VARCHAR(100),
                             region VARCHAR(100),
                             sitio_web VARCHAR(200),
                             estado VARCHAR(20) NOT NULL,
                             fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                             fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                             CONSTRAINT fk_proveedores_empresa
                                 FOREIGN KEY (empresa_id)
                                     REFERENCES empresas(id),

                             CONSTRAINT uk_proveedores_empresa_rut
                                 UNIQUE (empresa_id, rut)
);