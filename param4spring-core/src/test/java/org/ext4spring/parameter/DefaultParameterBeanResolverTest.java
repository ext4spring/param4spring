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

import java.util.Map;

import org.ext4spring.parameter.annotation.Parameter;
import org.ext4spring.parameter.annotation.ParameterBean;
import org.ext4spring.parameter.annotation.ParameterQualifier;
import org.ext4spring.parameter.converter.tv.TVConverter;
import org.ext4spring.parameter.model.ParameterBeanMetadata;
import org.junit.Assert;
import org.junit.Test;

public class DefaultParameterBeanResolverTest extends TestBase {

    @SuppressWarnings("unused")
    private class NotAnnotatedParameterBean {
        Long longParam;

        public Long getLongParam() {
            return longParam;
        }

        public void setLongParam(Long longParam) {
            this.longParam = longParam;
        }
    }

    @ParameterBean(domain = "testDomain")
    private class AnnotatedParameterBean {
        Long longParam;

        @Parameter(name = "longer")
        public Long getLongParam() {
            return longParam;
        }

        double doubleParam;

        @Parameter(name = "double", defaultValue = "10.5")
        public double getDoubleParam() {
            return doubleParam;
        }

        String optionalStringParam;

        @Parameter(optional = true)
        public String getOptionalStringParam() {
            return optionalStringParam;
        }
        
        Map<String, String> qualifiedParam;

        @SuppressWarnings("unused")
        public String getQualifiedParam(@ParameterQualifier String qualifier) {
            return qualifiedParam.get(qualifier);
        }

        boolean value;
        
        @Parameter(converter = TVConverter.class, defaultValue = "true", optional = true, name = "differentName")
        public Boolean isValue() {
            return false;
        }

        @SuppressWarnings("unused")
        public void setValue(Boolean value) {

        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testResolveWithoutAnnotationInfo() throws Exception {
        Class notAnnotatedClass = Class.forName(NotAnnotatedParameterBean.class.getName());

        DefaultParameterBeanResolver parameterBeanResolver = new DefaultParameterBeanResolver();
        parameterBeanResolver.setParameterResolver(new DefaultParameterResolver());
        ParameterBeanMetadata beanMetadata = parameterBeanResolver.parse(notAnnotatedClass);
        Assert.assertEquals("org.ext4spring.parameter.DefaultParameterBeanResolverTest.NotAnnotatedParameterBean", beanMetadata.getDomain());
        Assert.assertEquals(1, beanMetadata.getParameters().size());
        Assert.assertNotNull(beanMetadata.getParameter("LongParam"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testResolveWithAnnotationInfo() throws Exception {
        Class annotatedClass = Class.forName(AnnotatedParameterBean.class.getName());

        DefaultParameterBeanResolver parameterBeanResolver = new DefaultParameterBeanResolver();
        parameterBeanResolver.setParameterResolver(new DefaultParameterResolver());
        ParameterBeanMetadata beanMetadata = parameterBeanResolver.parse(annotatedClass);
        Assert.assertEquals("testDomain", beanMetadata.getDomain());
        Assert.assertEquals(5, beanMetadata.getParameters().size());
        Assert.assertNotNull(beanMetadata.getParameter("longer"));
        Assert.assertNotNull(beanMetadata.getParameter("OptionalStringParam"));
        Assert.assertNotNull(beanMetadata.getParameter("double"));
        Assert.assertNotNull(beanMetadata.getParameter("QualifiedParam"));
        Assert.assertNotNull(beanMetadata.getParameter("differentName"));

        //TODO: test writeable metadata if ParameterMetadata object contains it
    }

}
