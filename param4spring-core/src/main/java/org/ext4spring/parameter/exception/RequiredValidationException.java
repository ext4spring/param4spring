package org.ext4spring.parameter.exception;

public class RequiredValidationException extends ParameterValidationException {

    private static final long serialVersionUID = 1L;

    public RequiredValidationException(String message) {
        super(message);
    }

    public RequiredValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
