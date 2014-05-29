package org.ext4spring.parameter.exception;

public class ValueOutOfBoundsValidationException extends ParameterValidationException {

    private static final long serialVersionUID = 1L;

    public ValueOutOfBoundsValidationException(String message) {
        super(message);
    }

    public ValueOutOfBoundsValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
