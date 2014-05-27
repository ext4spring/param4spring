package org.ext4spring.parameter;

import java.util.Set;

import org.ext4spring.parameter.exception.ParameterException;

/**
 * Registry for parameter bean classes If you use the API with AOP, this is automatically populated. If you only use
 * {@link ParameterBeanService} to read parameters, you should first manually register your beans manually
 * 
 * @author pborbas
 */
public interface ParameterBeanRegistry {
    /**
     * Registers a class as parameter bean.
     * 
     * @param parameterBeanClass
     * @throws ParameterException
     */
    public void register(Class<?> parameterBeanClass) throws ParameterException;

    /**
     * List all registered parameter beans
     * 
     * @return
     */
    public Set<Class<?>> listParameterBeanClasses();
}
