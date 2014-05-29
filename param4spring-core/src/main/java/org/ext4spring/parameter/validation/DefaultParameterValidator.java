package org.ext4spring.parameter.validation;

import org.ext4spring.parameter.annotation.ParameterValidation;
import org.ext4spring.parameter.exception.LengthOutOfBoundsValidationException;
import org.ext4spring.parameter.exception.ParameterValidationException;
import org.ext4spring.parameter.exception.ReadOnlyValidationException;
import org.ext4spring.parameter.exception.RequiredValidationException;
import org.ext4spring.parameter.exception.ValueOutOfBoundsValidationException;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.springframework.stereotype.Component;
/**
 * Default validator for parameter beans if validation is enabled. Validates {@link ParameterValidation} parameters
 * @author pborbas
 *
 */
@Component
public class DefaultParameterValidator implements ParameterValidator {

    @Override
    public void validate(ParameterMetadata metadata, Object value, Object parameterObject) throws ParameterValidationException {
        if (metadata.isReadOnly()) {
            throw new ReadOnlyValidationException("Parameter is read only. Modification not allowed:" + metadata);
        }
        if (!metadata.isOptional() && metadata.getDefaultValue() == null && value == null) {
            throw new RequiredValidationException("Null value not allowed for parameter:" + metadata);
        }
        if (value != null && value instanceof Number) {
            Number numberValue = (Number) value;
            if (metadata.getMax() != null && numberValue.doubleValue() > metadata.getMax()) {
                throw new ValueOutOfBoundsValidationException("Value is bigger that the maximum. Value:" + numberValue + " allowed:" + metadata.getMax() + "." + metadata);
            }
            if (metadata.getMin() != null && numberValue.doubleValue() < metadata.getMin()) {
                throw new ValueOutOfBoundsValidationException("Value is less that the minimum. Value:" + numberValue + " allowed:" + metadata.getMin() + "." + metadata);
            }
        } else if (value != null && value instanceof String) {
            String stringValue = (String) value;
            if (metadata.getMax() != null && stringValue.length() > metadata.getMax()) {
                throw new LengthOutOfBoundsValidationException("Value is longer that the maximum. Length:" + stringValue.length() + " allowed:" + metadata.getMax() + "." + metadata);
            }
            if (metadata.getMin() != null && stringValue.length() < metadata.getMin()) {
                throw new LengthOutOfBoundsValidationException("Value is shorter that the minimum. Length:" + stringValue.length() + " allowed:" + metadata.getMin() + "." + metadata);
            }
        }
    }

}
