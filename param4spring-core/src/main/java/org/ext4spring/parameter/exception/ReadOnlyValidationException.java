package org.ext4spring.parameter.exception;

public class ReadOnlyValidationException extends ParameterValidationException {

    private static final long serialVersionUID = 1L;

    public ReadOnlyValidationException(String message) {
        super(message);
    }

    public ReadOnlyValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
