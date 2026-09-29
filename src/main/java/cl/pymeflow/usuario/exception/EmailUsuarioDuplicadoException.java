package cl.pymeflow.usuario.exception;

public class EmailUsuarioDuplicadoException extends RuntimeException {

    public EmailUsuarioDuplicadoException(String email) {
        super("Ya existe un usuario registrado con el email: " + email);
    }
}