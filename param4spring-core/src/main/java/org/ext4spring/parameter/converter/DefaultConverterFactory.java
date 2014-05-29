package org.ext4spring.parameter.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ext4spring.parameter.SpringComponents;
import org.ext4spring.parameter.exception.ParameterConverterException;
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
@Component(SpringComponents.defaultConverterFactory)
public class DefaultConverterFactory implements ConverterFactory, ApplicationContextAware {
    private static final Log LOGGER = LogFactory.getLog(DefaultConverterFactory.class);
    private ApplicationContext applicationContext;

    @Override
    public <T extends Converter> T get(Class<T> converterClass) throws ParameterConverterException {
        try {
            return this.applicationContext.getBean(converterClass);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.debug("Converter not found in spring context:" + converterClass + ". Creating with reflection.");
            try {
                return converterClass.newInstance();
            } catch (InstantiationException e1) {
                throw new ParameterConverterException("Cannot find Converter in spring context and instantiation failed:" + e, e);
            } catch (IllegalAccessException e1) {
                throw new ParameterConverterException("Cannot find Converter in spring context and instantiation failed:" + e, e);
            }

        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
