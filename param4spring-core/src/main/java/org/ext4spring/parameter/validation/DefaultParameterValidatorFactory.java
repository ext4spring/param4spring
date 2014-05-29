package org.ext4spring.parameter.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ext4spring.parameter.DefaultParameterBeanService;
import org.ext4spring.parameter.SpringComponents;
import org.ext4spring.parameter.exception.ParameterValidationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Tries to get the validator instance from the Spring context. If its not there it tries to create an instance with
 * reflection
 * 
 * @author pborbas
 */
@Component(SpringComponents.defaultParameterValidatorFactory)
public class DefaultParameterValidatorFactory implements ParameterValidatorFactory, ApplicationContextAware {
    private static final Log LOGGER = LogFactory.getLog(DefaultParameterBeanService.class);
    private ApplicationContext applicationContext;

    /**
     * Tries to get the validator instance from the Spring context. If its not there it tries to create an instance with
     * reflection
     */
    @Override
    public <T extends ParameterValidator> T getValidator(Class<T> validatorClass) {
        try {
            return this.applicationContext.getBean(validatorClass);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.debug("Validator not found in spring context:" + validatorClass + ". Creating with reflection.");
            try {
                return validatorClass.newInstance();
            } catch (InstantiationException e1) {
                throw new ParameterValidationException("Cannot find validator in spring context and instantiation failed:" + e, e);
            } catch (IllegalAccessException e1) {
                throw new ParameterValidationException("Cannot find validator in spring context and instantiation failed:" + e, e);
            }

        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
