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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ext4spring.parameter.exception.ParameterException;
import org.ext4spring.parameter.exception.ParameterUndefinedException;
import org.ext4spring.parameter.model.ParameterBeanMetadata;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultParameterBeanService implements ParameterBeanService, ApplicationContextAware {

    private static final Log LOGGER = LogFactory.getLog(DefaultParameterBeanService.class);

    private ParameterResolver parameterResolver;
    private ParameterService parameterService;
    private ParameterBeanRegistry parameterBeanRegistry;
    private ParameterBeanResolver parameterBeanResolver;
    private ApplicationContext applicationContext;

    private List<Field> getSupportedFields(Class<?> clazz) {
        List<Field> supportedFields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) { //!Modifier.isFinal(modifiers) && 
                supportedFields.add(field);
            }
        }
        return supportedFields;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public <T> T readParameterBean(Class<T> typeClass) throws ParameterException {
        return this.readParameterBean(typeClass, null);
    }

    private Field findField(Class<?> typeClass, String fieldName) throws ParameterUndefinedException {
        try {
            return typeClass.getDeclaredField(fieldName);
        } catch (SecurityException e) {
            throw new ParameterUndefinedException("Parameter field:" + fieldName + " not found in class:" + typeClass + ". Error:" + e);
        } catch (NoSuchFieldException e) {
            throw new ParameterUndefinedException("Parameter field:" + fieldName + " not found in class:" + typeClass + ". Error:" + e);
        }
//                for (Field field : typeClass.getFields()) {
//            if (field.getName().equalsIgnoreCase(fieldName)) {
//                return field;
//            }
//        }
//        throw new ParameterUndefinedException("Parameter field:" + fieldName + " not found in class:" + typeClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readParameterBean(Class<T> typeClass, String parameterQualifier) throws ParameterException {
        LOGGER.debug("Reading parameters for class:" + typeClass + ", qualifier:" + parameterQualifier);
        ParameterBeanMetadata beanMetadata = this.parameterBeanResolver.parse(typeClass);
        T paramterBean;
        try {
            paramterBean = typeClass.newInstance();
            for (ParameterMetadata parameterMetadata : beanMetadata.getParameters()) {
                Field field = this.findField(typeClass, parameterMetadata.getAttribute());
                field.setAccessible(true);
                if (parameterMetadata.isQualified()) {
                    parameterMetadata.setQualifier(parameterQualifier);
                 }
                Object value = this.parameterService.read(parameterMetadata, field.get(paramterBean));
                field.set(paramterBean, value);
                field.setAccessible(false);

            }

//            for (Field field : this.getSupportedFields(typeClass)) {
//                // find getter for field;
//                for (Method method : paramterBean.getClass().getMethods()) {
//                    if ((method.getName().startsWith("get") && method.getName().substring(3).toLowerCase().equals(field.getName().toLowerCase()))
//                            || (method.getName().startsWith("is") && method.getName().substring(2).toLowerCase().equals(field.getName().toLowerCase()))) {
//                        // set value from repository
//                        field.setAccessible(true);
//                        ParameterMetadata metadata = this.parameterResolver.parse(method, null);
//                        if (metadata.isQualified()) {
//                            metadata.setQualifier(parameterQualifier);
//                        }
//                        Object value = this.parameterService.read(metadata, field.get(paramterBean));
//                        field.set(paramterBean, value);
////                        if (metadata.isQualified() && parameterQualifier!=null) {
////                            metadata.setQualifier(parameterQualifier);
////                            Object value = this.parameterService.read(metadata, field.get(paramterBean));
////                            field.set(paramterBean, value);
////                               try {
////                                    Map<String, Object> qualifiedMap;
////                                    Object mapField = field.get(paramterBean);
////                                    if (mapField == null) {
////                                        qualifiedMap = new HashMap<String, Object>();
////                                    } else {
////                                        qualifiedMap = (Map<String, Object>) field.get(paramterBean);
////                                    }
////                                    metadata.setQualifier(parameterQualifier);
////                                    Object value = this.parameterService.read(metadata, field.get(paramterBean));
////                                    qualifiedMap.put(parameterQualifier, value);
////                                } catch (ClassCastException e) {
////                                    throw new ParameterException("If this field is a qualified field, the field type should be java.util.Map", e);
////                                }
////                        } else {
////                            Object value = this.parameterService.read(metadata, field.get(paramterBean));
////                            field.set(paramterBean, value);
////                        }
//
//                    }
//                    field.setAccessible(false);
//                }
//            }
            this.register(typeClass);
            return paramterBean;
        } catch (Exception e) {
            LOGGER.error("Error happened while reading parameter bean:" + typeClass + "." + e, e);
            throw new ParameterException("Error happened while reading parameter bean:" + typeClass + "." + e, e);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public <T> void writeParameterBean(T parameterBean) {
        LOGGER.debug("Writing parameters for bean:" + parameterBean);
        try {
            for (Field field : this.getSupportedFields(parameterBean.getClass())) {
                // find getter for field;
                for (Method method : parameterBean.getClass().getMethods()) {
                    if (method.getName().startsWith("set") && method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                        // save value to repository
                        field.setAccessible(true);
                        ParameterMetadata metadata = this.parameterResolver.parse(method, null);
                        if (metadata.isQualified()) {
                            //TODO: If the parameter is qualified get the Map and save all values to the repository
                            throw new UnsupportedOperationException("Saving of parameter beans with @ParameterQualifier annotation is not yet supported");
                        }
                        this.parameterService.write(metadata, field.get(parameterBean));
                        field.setAccessible(false);
                    }
                }
            }
            this.register(parameterBean.getClass());
        } catch (Exception e) {
            LOGGER.error("Error happened while writing parameter bean:" + parameterBean + "." + e, e);
            throw new ParameterException("Error happened while writing parameter bean:" + parameterBean + "." + e, e);
        }
    }

    @Override
    public void register(Class<?> parameterBeanClass) throws ParameterException {
        this.parameterBeanRegistry.register(parameterBeanClass);
    }

    @Override
    public List<ParameterBeanMetadata> listParameterBeans() throws ParameterException {
        List<ParameterBeanMetadata> parameterBeanMetadatas = new ArrayList<ParameterBeanMetadata>();
        Set<Class<?>> parameterBeanClasses = this.parameterBeanRegistry.listParameterBeanClasses();
        for (Class<?> parameterBeanClass : parameterBeanClasses) {
            parameterBeanMetadatas.add(this.parameterBeanResolver.parse(parameterBeanClass));
        }
        return parameterBeanMetadatas;
    }

    public void setParameterResolver(ParameterResolver parameterResolver) {
        this.parameterResolver = parameterResolver;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setParameterBeanRegistry(ParameterBeanRegistry parameterBeanRegistry) {
        this.parameterBeanRegistry = parameterBeanRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        if (this.parameterResolver == null) {
            this.parameterResolver = (ParameterResolver) this.applicationContext.getBean(SpringComponents.defaultParameterResolver);
        }
        if (this.parameterBeanRegistry == null) {
            this.parameterBeanRegistry = (ParameterBeanRegistry) this.applicationContext.getBean(SpringComponents.defaultParameterBeanRegistry);
        }
        if (this.parameterBeanResolver == null) {
            this.parameterBeanResolver = (ParameterBeanResolver) this.applicationContext.getBean(SpringComponents.defaultParameterBeanResolver);
        }
        if (this.parameterService == null) {
            this.parameterService = this.applicationContext.getBean(ParameterService.class);
        }

    }

}
