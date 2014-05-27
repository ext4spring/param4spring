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

import javax.annotation.PostConstruct;

import org.ext4spring.parameter.annotation.ParameterBean;
import org.ext4spring.parameter.model.ParameterBeanMetadata;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component(SpringComponents.defaultParameterBeanResolver)
public class DefaultParameterBeanResolver implements ParameterBeanResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private ParameterResolver parameterResolver;

    @Override
    public ParameterBeanMetadata parse(Class<?> parameterClass) {
        ParameterBeanMetadata beanMetadata = new ParameterBeanMetadata();
        beanMetadata.setParameterBeanClass(parameterClass);
        beanMetadata.setDomain(this.resolveDomain(parameterClass));
        for (Field field : this.getSupportedFields(parameterClass)) {
            for (Method method : parameterClass.getMethods()) {
                if ((method.getName().startsWith("get") && method.getName().substring(3).toLowerCase().equals(field.getName().toLowerCase()))
                        || (method.getName().startsWith("is") && method.getName().substring(2).toLowerCase().equals(field.getName().toLowerCase()))) {
                    ParameterMetadata metadata = this.parameterResolver.parse(method, null);
                    beanMetadata.addParameter(metadata);
                    if (metadata.isQualified()) {
                        beanMetadata.setQualified(true);
                    }          
                }
            }
        }
        return beanMetadata;
    }
    
    @Override
    public Field findFieldForParameter(Object parameterBean, ParameterMetadata parameterMetadata) {
        for (Field field:this.getSupportedFields(parameterBean.getClass())) {
            if (parameterMetadata.getFullParameterName().equalsIgnoreCase(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public List<Field> getSupportedFields(Class<?> clazz) {
        List<Field> supportedFields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) { //!Modifier.isFinal(modifiers) && 
                supportedFields.add(field);
            }
        }
        return supportedFields;
    }

    private String resolveDomain(Class<?> parameterClass) {
        String domain;
        if (parameterClass.isAnnotationPresent(ParameterBean.class) && !ParameterBean.UNDEFINED.equals(parameterClass.getAnnotation(ParameterBean.class).domain())) {
            domain = parameterClass.getAnnotation(ParameterBean.class).domain();
        } else {
            domain = parameterClass.getCanonicalName();
        }
        return domain;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setParameterResolver(ParameterResolver parameterResolver) {
        this.parameterResolver = parameterResolver;
    }

    @PostConstruct
    public void init() {
        if (this.parameterResolver == null) {
            this.parameterResolver = (ParameterResolver) this.applicationContext.getBean(SpringComponents.defaultParameterResolver);
        }
    }

}
