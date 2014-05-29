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

import java.lang.reflect.Method;

import org.ext4spring.parameter.annotation.Parameter;
import org.ext4spring.parameter.annotation.ParameterBean;
import org.ext4spring.parameter.annotation.ParameterValidation;
import org.ext4spring.parameter.converter.tv.TVConverter;
import org.ext4spring.parameter.exception.LengthOutOfBoundsValidationException;
import org.ext4spring.parameter.exception.ReadOnlyValidationException;
import org.ext4spring.parameter.exception.RequiredValidationException;
import org.ext4spring.parameter.exception.ValueOutOfBoundsValidationException;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.ext4spring.parameter.validation.DefaultParameterValidator;
import org.junit.Before;
import org.junit.Test;

public class DefaultParameterValidatorTest extends TestBase {

    @ParameterBean(domain = "testDomain")
    private class AnnotatedParameterBean {

        double optionalDoubleParam;

        @Parameter(defaultValue="10")
        @ParameterValidation(min = 10, max = 15, optional = true)
        public double getOptionalDoubleParam() {
            return optionalDoubleParam;
        }
        
        public void setOptionalDoubleParam(double optionalDoubleParam) {
            this.optionalDoubleParam = optionalDoubleParam;
        }
        
        String requiredString;

        @ParameterValidation(min = 10, max = 15, optional = false)
        public String getRequiredString() {
            return requiredString;
        }
        
        public void setRequiredString(String requiredString) {
            this.requiredString = requiredString;
        }
        
        String readOnlyString;
        public String getReadOnlyString() {
            return readOnlyString;
        }
    }

    private Class annotatedClass;
    private DefaultParameterResolver defaultParameterResolver;
    DefaultParameterValidator validator=new DefaultParameterValidator();
    
    @Before
    public void init() throws ClassNotFoundException {
        annotatedClass = Class.forName(AnnotatedParameterBean.class.getName());
        defaultParameterResolver=new DefaultParameterResolver();
        defaultParameterResolver.setDefaultConverter(new TVConverter());
    }
    
    @Test(expected=ValueOutOfBoundsValidationException.class)
    @SuppressWarnings({ "unchecked" })
    public void testLessThanAllowed() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Method annotatedLongMethod = annotatedClass.getMethod("getOptionalDoubleParam");
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        validator.validate(metadata, 0d, null);
    }

    @Test(expected=ValueOutOfBoundsValidationException.class)
    @SuppressWarnings({ "unchecked" })
    public void testMoreThanAllowed() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Method annotatedLongMethod = annotatedClass.getMethod("getOptionalDoubleParam");
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        validator.validate(metadata, 20d, null);
    }
    
    @Test(expected=LengthOutOfBoundsValidationException.class)
    @SuppressWarnings({ "unchecked"})
    public void testShorterThanAllowed() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Method annotatedLongMethod = annotatedClass.getMethod("getRequiredString");
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        validator.validate(metadata, "short", null);
    }

    @Test(expected=LengthOutOfBoundsValidationException.class)
    @SuppressWarnings({ "unchecked"})
    public void testLongerThanAllowed() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Method annotatedLongMethod = annotatedClass.getMethod("getRequiredString");
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        validator.validate(metadata, "loooooooooooooooooooooooooooooooooooooooooong", null);
    }

    @Test(expected=RequiredValidationException.class)
    @SuppressWarnings({ "unchecked"})
    public void testRequired() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Method annotatedLongMethod = annotatedClass.getMethod("getRequiredString");
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        validator.validate(metadata, null , null);
    }

    @Test(expected=ReadOnlyValidationException.class)
    @SuppressWarnings({ "unchecked"})
    public void testReadOnly() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Method annotatedLongMethod = annotatedClass.getMethod("getReadOnlyString");
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        validator.validate(metadata, "new value" , null);
    }


}
