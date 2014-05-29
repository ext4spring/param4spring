package org.ext4spring.parameter.exception;
/**
 * Thrown if a required parameter has null value
 */
public class ParameterValidationException extends ParameterException{

    private static final long serialVersionUID = -3706825790421580912L;

    public ParameterValidationException(String message) {
        super(message);
    }

    public ParameterValidationException(String message, Throwable cause) {
        super(message, cause);
    }


}
