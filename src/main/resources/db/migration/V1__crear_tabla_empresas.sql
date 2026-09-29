CREATE TABLE empresas (
                          id UUID PRIMARY KEY,

                          rut VARCHAR(12) NOT NULL,
                          razon_social VARCHAR(150) NOT NULL,
                          nombre_fantasia VARCHAR(150),

                          email VARCHAR(150),
                          telefono VARCHAR(30),

                          direccion VARCHAR(200),
                          comuna VARCHAR(100),
                          region VARCHAR(100),

                          estado VARCHAR(20) NOT NULL,

                          fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                          fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT uk_empresas_rut UNIQUE (rut)
);