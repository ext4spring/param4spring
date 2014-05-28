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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.ext4spring.parameter.annotation.Parameter;
import org.ext4spring.parameter.annotation.ParameterBean;
import org.ext4spring.parameter.annotation.ParameterQualifier;
import org.ext4spring.parameter.converter.Converter;
import org.ext4spring.parameter.model.Operation;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component(SpringComponents.defaultParameterResolver)
public class DefaultParameterResolver implements ParameterResolver {

    // map for change primitives to objects
    private final Map<String, Class<?>> typeMap = new HashMap<String, Class<?>>();

    public DefaultParameterResolver() {
        typeMap.put("byte", Byte.class);
        typeMap.put("short", Short.class);
        typeMap.put("int", Integer.class);
        typeMap.put("long", Long.class);
        typeMap.put("float", Float.class);
        typeMap.put("double", Double.class);
        typeMap.put("boolean", Boolean.class);
        typeMap.put("char", Character.class);
    }

    @Override
    public ParameterMetadata parse(Method method, Object[] invocationArgumnets) {
        ParameterMetadata metadata = new ParameterMetadata();
        // domain
        metadata.setDomain(this.resolveDomain(method));
        // parameter name and operation
        metadata.setAttribute(this.getFieldNameByMethod(method));
        metadata.setTypeClass(this.resolveParameterType(method));
        metadata.setParameter(this.resolveParameterName(method, metadata.getAttribute()));
        metadata.setConverter(this.resolveConverter(method, metadata.getAttribute()));
        metadata.setOptional(this.resolveOptional(method, metadata.getAttribute()));
        metadata.setDefaultValue(this.resolveDefaultValue(method, metadata.getAttribute()));
        metadata.setReadOnly(this.isReadOnly(method, metadata.getAttribute()));
        this.resolveQualifier(metadata, method, invocationArgumnets);
        return metadata;
    }

    private String[] validMethodPrefixes = new String[] { "is", "get", "set" };

    @Override
    public String getFieldNameByMethod(Method method) {
        for (String methodPrefix : validMethodPrefixes) {
            if (method.getName().startsWith(methodPrefix)) {
                String nameWithoutPrefix = method.getName().substring(methodPrefix.length());
                return StringUtils.uncapitalize(nameWithoutPrefix);
            }
        }
        return null;
    }

    /**
     * @param method
     * @param attributeName
     * @return true if method is a setter or there is a setter for the getter
     */
    protected boolean isReadOnly(Method method, String attributeName) {
        Operation operation = Operation.valueOfByMethodName(method.getName());
        if (Operation.WRITE.equals(operation)) {
            return false;
        } else {
            String methodWithoutPrefix = StringUtils.capitalize(attributeName);
            for (Method currMethod : method.getDeclaringClass().getMethods()) {
                if (currMethod.getName().equals("set" + methodWithoutPrefix)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected <T extends Annotation> T findAnnotation(Class<T> annotationType, Method method, String attributeName) {
        T annotation = method.getAnnotation(annotationType);
        if (annotation == null) {
            //not found on the specified method. for setters it looks for getter annotations
            Operation operation = Operation.valueOfByMethodName(method.getName());
            String methodWithoutPrefix = StringUtils.capitalize(attributeName);
            if (Operation.WRITE.equals(operation)) {
                for (Method currMethod : method.getDeclaringClass().getMethods()) {
                    if (currMethod.getName().endsWith(methodWithoutPrefix)) {
                        if (currMethod.getAnnotation(annotationType) != null) {
                            return currMethod.getAnnotation(annotationType);
                        }
                    }
                }
            }
        }
        return annotation;
    }

    protected void resolveQualifier(ParameterMetadata metadata, Method method, Object[] invocationArgumnets) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int paramIndex = 0; paramIndex < paramAnnotations.length; paramIndex++) {
            {
                for (Annotation annotation : paramAnnotations[paramIndex]) {
                    if (annotation instanceof ParameterQualifier) {
                        metadata.setQualified(true);
                        if (invocationArgumnets != null && invocationArgumnets[paramIndex] != null) {
                            metadata.setQualifier(invocationArgumnets[paramIndex].toString());
                        }
                    }
                }
            }
        }
    }

    private boolean resolveOptional(Method method, String attributeName) {
        Parameter parameterAnnotation = this.findAnnotation(Parameter.class, method, attributeName);
        if (parameterAnnotation != null) {
            return parameterAnnotation.optional();
        }
        return false;
    }

    private Class<? extends Converter> resolveConverter(Method method, String attributeName) {
        Parameter parameterAnnotation = this.findAnnotation(Parameter.class, method, attributeName);
        if (parameterAnnotation != null && parameterAnnotation.converter().length > 0) {
            return parameterAnnotation.converter()[0];
        }
        return null;
    }

    private String resolveDefaultValue(Method method, String attributeName) {
        Parameter parameterAnnotation = this.findAnnotation(Parameter.class, method, attributeName);
        if (parameterAnnotation != null && !parameterAnnotation.defaultValue().equals(Parameter.UNDEFINED)) {
            return parameterAnnotation.defaultValue();
        }
        return null;
    }

    private String resolveParameterName(Method method, String attributeName) {
        String name;
        Parameter parameterAnnotation = this.findAnnotation(Parameter.class, method, attributeName);
        if (parameterAnnotation != null && !parameterAnnotation.name().equals(Parameter.UNDEFINED)) {
            name = parameterAnnotation.name();
        } else {
            name = StringUtils.capitalize(attributeName);
        }
        return name;
    }

    private Class<?> resolveParameterType(Method method) {
        Operation operation = Operation.valueOfByMethodName(method.getName());
        if (Operation.WRITE.equals(operation)) {
            return method.getParameterTypes()[0];
        } else {
            Class<?> type = method.getReturnType();
            if (type.isPrimitive()) {
                return this.typeMap.get(type.getName());
            } else {
                return type;
            }
        }
    }

    private String resolveDomain(Method method) {
        String domain;
        if (method.getDeclaringClass().isAnnotationPresent(ParameterBean.class) && !ParameterBean.UNDEFINED.equals(method.getDeclaringClass().getAnnotation(ParameterBean.class).domain())) {
            domain = method.getDeclaringClass().getAnnotation(ParameterBean.class).domain();
        } else {
            domain = method.getDeclaringClass().getCanonicalName();
        }
        return domain;
    }
}
