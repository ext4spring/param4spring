package org.ext4spring.parameter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a parameter bean with its parameters
 * 
 * @author pborbas
 *
 */
public class ParameterBeanMetadata {

    private boolean qualified;
    private Class<?> parameterBeanClass;
    private String domain;
    private String comment;
    private final List<ParameterMetadata> parameters=new ArrayList<ParameterMetadata>(); 
    
    public String getDomain() {
        return domain;
    }
    
    public boolean isQualified() {
        return qualified;
    }
    
    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public List<ParameterMetadata> getParameters() {
        return parameters;
    }
    
    public ParameterMetadata getParameter(String parameterName) {
        for (ParameterMetadata metadata:this.parameters) {
            if (metadata.getParameter().equals(parameterName) || parameterName.equals(metadata.getAttribute())) {
                return metadata;
            }
        }
        return null;
    }
    
    public void addParameter(ParameterMetadata parameterMetadata) {
        this.parameters.add(parameterMetadata);
    }

    public Class<?> getParameterBeanClass() {
        return parameterBeanClass;
    }
    
    public void setParameterBeanClass(Class<?> parameterBeanClass) {
        this.parameterBeanClass = parameterBeanClass;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}
