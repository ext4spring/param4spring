package org.ext4spring.parameter;

import java.util.HashSet;
import java.util.Set;

import org.ext4spring.parameter.exception.ParameterException;
import org.springframework.stereotype.Component;

@Component(SpringComponents.defaultParameterBeanRegistry)
public class DefaultParameterBeanRegistry implements ParameterBeanRegistry {

    private final Set<Class<?>> parameterBeanClasses=new HashSet<Class<?>>();
    
    @Override
    public void register(Class<?> parameterBeanClass) throws ParameterException {
        parameterBeanClasses.add(parameterBeanClass);
    }

    @Override
    public Set<Class<?>> listParameterBeanClasses() {
        return parameterBeanClasses;
    }

}
