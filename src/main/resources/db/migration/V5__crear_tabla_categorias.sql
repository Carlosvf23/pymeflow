CREATE TABLE categorias (
                            id UUID PRIMARY KEY,
                            empresa_id UUID NOT NULL,
                            nombre VARCHAR(100) NOT NULL,
                            descripcion VARCHAR(300),
                            estado VARCHAR(20) NOT NULL,
                            fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
                            fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,

                            CONSTRAINT fk_categorias_empresa
                                FOREIGN KEY (empresa_id)
                                    REFERENCES empresas(id),

                            CONSTRAINT uk_categorias_empresa_nombre
                                UNIQUE (empresa_id, nombre)
);