CREATE TABLE usuarios (
                          id UUID PRIMARY KEY,

                          empresa_id UUID NOT NULL,

                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,

                          email VARCHAR(150) NOT NULL,
                          password VARCHAR(255) NOT NULL,

                          rol VARCHAR(30) NOT NULL,
                          estado VARCHAR(20) NOT NULL,

                          fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                          fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT uk_usuarios_email UNIQUE (email),

                          CONSTRAINT fk_usuarios_empresa
                              FOREIGN KEY (empresa_id)
                                  REFERENCES empresas(id)
);