package org.ext4spring.parameter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ext4spring.parameter.exception.ParameterException;
import org.springframework.stereotype.Component;

@Component(SpringComponents.defaultParameterBeanRegistry)
public class DefaultParameterBeanRegistry implements ParameterBeanRegistry {

    private static final Log LOGGER = LogFactory.getLog(DefaultParameterBeanRegistry.class);
    private final Set<Class<?>> parameterBeanClasses = new HashSet<Class<?>>();

    @Override
    public void register(Class<?> parameterBeanClass) throws ParameterException {
        if (!parameterBeanClasses.contains(parameterBeanClass)) {
            LOGGER.info("Class:" + parameterBeanClass + " registered as parameter bean");
            parameterBeanClasses.add(parameterBeanClass);
        }
    }

    @Override
    public Set<Class<?>> listParameterBeanClasses() {
        return parameterBeanClasses;
    }

}
