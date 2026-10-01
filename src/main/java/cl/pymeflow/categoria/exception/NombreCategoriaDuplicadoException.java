package cl.pymeflow.categoria.exception;

public class NombreCategoriaDuplicadoException extends RuntimeException {

    public NombreCategoriaDuplicadoException(String nombre) {
        super("Ya existe una categoría con el nombre: " + nombre);
    }
}