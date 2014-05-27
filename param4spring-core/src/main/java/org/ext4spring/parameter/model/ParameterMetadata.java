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

import org.ext4spring.parameter.converter.Converter;

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
    private Class<? extends Converter> converter;
    private boolean optional;
    private boolean qualified;
    private String qualifier;

    /**
     * parameter name with its qualifier (if has one)
     * 
     * @return
     */
    public String getFullParameterName() {
        //local variable is necassary for json serialization, so thats why we set the value instead of simply return
        if (qualifier != null) {
            this.fullParameterName = parameter + "." + qualifier;
        } else {
            this.fullParameterName = parameter;
        }
        return this.fullParameterName;
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
    }

    @Override
    public String toString() {
        return "Metadata [typeClass=" + typeClass + ", domain=" + domain + ", attribute=" + attribute + ", parameter=" + parameter + ", defaultValue=" + defaultValue + ", converter=" + converter
                + ", optional=" + optional + ", qualified=" + qualified + ", qualifier=" + qualifier + "]";
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

}
