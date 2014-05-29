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
package org.ext4spring.parameter.model;

import java.util.ArrayList;
import java.util.List;

import org.ext4spring.parameter.converter.Converter;
import org.ext4spring.parameter.validation.ParameterValidator;

/**
 * Describes one parameter of a parameter bean
 * 
 * @author Peter Borbas
 */
public class ParameterMetadata {
    private Class<?> typeClass;
    private String domain;
    private String attribute;
    private String parameter;
    private String fullParameterName;
    private String defaultValue;
    private String comment;
    private Class<? extends Converter> converter;
    private boolean optional;
    private boolean qualified;
    private String qualifier;
    private boolean readOnly;
    private Double min;
    private Double max;
    private String format;
    private List<Class<? extends ParameterValidator>> validators = new ArrayList<Class<? extends ParameterValidator>>();

    public static String parseQualifier(String fullParameterName, String parameterName) {
        return fullParameterName.substring(parameterName.length() + 1);
    }

    public static String createFullName(String parameterName, String qualifier) {
        return parameterName + "." + qualifier;
    }

    /**
     * parameter name with its qualifier (if has one)
     * 
     * @return
     */
    public String getFullParameterName() {
        return this.fullParameterName;
    }

    private void createFullParameterName() {
        if (qualifier != null) {
            this.fullParameterName = createFullName(parameter, qualifier);
        } else {
            this.fullParameterName = parameter;
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    //The attribute name of the parameter (field name of the object)
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    //The parameter name of the parameter equals with the capitalized attribute name if a @Parameter doesn't override it 
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
        this.createFullParameterName();
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends Converter> getConverter() {
        return converter;
    }

    public void setConverter(Class<? extends Converter> converter) {
        this.converter = converter;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
        this.createFullParameterName();
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<Class<? extends ParameterValidator>> getValidators() {
        return validators;
    }

    public void setValidators(List<Class<? extends ParameterValidator>> validators) {
        this.validators = validators;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
        result = prime * result + ((typeClass == null) ? 0 : typeClass.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ParameterMetadata [attribute=" + attribute + ", parameter=" + parameter + ", domain=" + domain + ", typeClass=" + typeClass + ", defaultValue=" + defaultValue + ", qualified="
                + qualified + ", qualifier=" + qualifier + ", optional=" + optional + ", readOnly=" + readOnly + ", min=" + min + ", max=" + max + ", format=" + format + ", converter=" + converter
                + ", validators=" + validators + "]";
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ParameterMetadata other = (ParameterMetadata) obj;
        if (domain == null) {
            if (other.domain != null) return false;
        } else if (!domain.equals(other.domain)) return false;
        if (parameter == null) {
            if (other.parameter != null) return false;
        } else if (!parameter.equals(other.parameter)) return false;
        if (typeClass == null) {
            if (other.typeClass != null) return false;
        } else if (!typeClass.equals(other.typeClass)) return false;
        return true;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
