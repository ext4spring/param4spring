package org.ext4spring.parameter.exception;

public class LengthOutOfBoundsValidationException extends ParameterValidationException {

    private static final long serialVersionUID = 1L;

    public LengthOutOfBoundsValidationException(String message) {
        super(message);
    }

    public LengthOutOfBoundsValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
