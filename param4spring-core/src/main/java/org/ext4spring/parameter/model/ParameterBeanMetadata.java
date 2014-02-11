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

    private String domain;
    private final List<ParameterMetadata> parameters=new ArrayList<ParameterMetadata>(); 
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public List<ParameterMetadata> getParameters() {
        return parameters;
    }
    
    public ParameterMetadata getParameter(String parameterName) {
        for (ParameterMetadata metadata:this.parameters) {
            if (metadata.getParameter().equals(parameterName)) {
                return metadata;
            }
        }
        return null;
    }
    
    public void addParameter(ParameterMetadata parameterMetadata) {
        this.parameters.add(parameterMetadata);
    }

}
