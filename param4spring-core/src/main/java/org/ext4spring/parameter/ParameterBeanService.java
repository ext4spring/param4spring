/*******************************************************************************
 * Copyright 2013 the original author
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ext4spring.parameter;

import java.util.List;
import java.util.Set;

import org.ext4spring.parameter.exception.ParameterException;
import org.ext4spring.parameter.model.ParameterBeanMetadata;

/**
 * Service to handle parameter beans without AOP
 * 
 * @author pborbas
 */
public interface ParameterBeanService {
    /**
     * Reads the parameter bean values from the configured repositories and populates the values into the bean fields
     * 
     * @param typeClass
     * @throws ParameterException
     */
    <T> T readParameterBean(Class<T> typeClass) throws ParameterException;

    /**
     * Reads the parameter bean, that has qualifier values from the configured repositories and populates the values
     * into the bean fields. The qualified parameter have to be java.util.Map. The Map keys will be the
     * parameterQualifiers values.
     * 
     * @param typeClass
     * @param parameterQualifiers qualifier that will be read from the repositories for @ParameterQualifier annotated
     *            methods.
     * @throws ParameterException
     */
    <T> T readParameterBean(Class<T> typeClass, String parameterQualifier) throws ParameterException;

    /**
     * Writes the parameter bean values into the underlying repositories.
     * 
     * @param parameterBean
     * @throws ParameterException
     */
    <T> void writeParameterBean(T parameterBean) throws ParameterException;

    /**
     * Writes the parameter bean values into the underlying repositories. For the qualified parameters uses the
     * parameterQualifier parameter.
     * 
     * @param parameterBean
     * @param parameterQualifier If it is null it saves to the default qualifier
     * @throws ParameterException
     */
    <T> void writeParameterBean(T parameterBean, String parameterQualifier) throws ParameterException;

    /**
     * Registers a class as parameter bean. read/writeParameterBean indirectly also registers bean and all AOP proxied
     * parameter beans are also registered automatically.
     * 
     * @param parameterBeanClass
     * @throws ParameterException
     */
    public void register(Class<?> parameterBeanClass) throws ParameterException;

    /**
     * List registered parameter beans If you don't use AOP parameter beans, you should register first manually.
     * 
     * @return
     * @throws ParameterException
     */
    List<ParameterBeanMetadata> listParameterBeans() throws ParameterException;

    /**
     * Unique list of all existing qualifiers in the repository for the given parameter class
     */
    Set<String> getQualifiers(Class<?> parameterBeanClass) throws ParameterException;

}
