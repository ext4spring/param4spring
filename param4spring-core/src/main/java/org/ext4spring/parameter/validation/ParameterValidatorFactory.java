package org.ext4spring.parameter.validation;

public interface ParameterValidatorFactory {

    /**
     * Returns an instance of the validator class
     * @param validatorClass
     * @return
     */
    <T extends ParameterValidator> T getValidator(Class<T> validatorClass);
}
