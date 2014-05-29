package org.ext4spring.parameter.validation;

import org.ext4spring.parameter.exception.ParameterValidationException;
import org.ext4spring.parameter.model.ParameterMetadata;

public interface ParameterValidator {
    /**
     * Validate a parameter before each write. User of the API can create custom implementations
     * 
     * @param metadata metadata of the modified parameter
     * @param value the new value of the parameter
     * @param parameterObject the configuration object instance that contains the parameter (useable for complex
     *            validation when the value depends on other value(s) of the same object)
     * @throws ParameterValidationException
     */
    public void validate(ParameterMetadata metadata, Object value, Object parameterObject) throws ParameterValidationException;
}
