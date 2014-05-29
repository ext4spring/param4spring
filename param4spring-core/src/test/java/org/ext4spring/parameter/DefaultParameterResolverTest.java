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

import org.ext4spring.parameter.annotation.Formats;
import org.ext4spring.parameter.annotation.Parameter;
import org.ext4spring.parameter.annotation.ParameterBean;
import org.ext4spring.parameter.annotation.ParameterComment;
import org.ext4spring.parameter.annotation.ParameterQualifier;
import org.ext4spring.parameter.annotation.ParameterValidation;
import org.ext4spring.parameter.converter.json.JSONConverter;
import org.ext4spring.parameter.converter.tv.TVConverter;
import org.ext4spring.parameter.exception.ParameterValidationException;
import org.ext4spring.parameter.model.Operation;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.ext4spring.parameter.validation.DefaultParameterValidator;
import org.ext4spring.parameter.validation.ParameterValidator;
import org.junit.Assert;
import org.junit.Test;

public class DefaultParameterResolverTest extends TestBase {

    @SuppressWarnings("unused")
    private class AllPrimitivesParameterBean {
        public byte getByte() {
            return 0;
        }

        public short getShort() {
            return 0;
        }

        public int getInt() {
            return 0;
        }

        public long getLong() {
            return 0;
        }

        public boolean isBoolean() {
            return false;
        }

        public float getFloat() {
            return 0;
        }

        public double getDouble() {
            return 0;
        }

        public char getChar() {
            return 0;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testResolvePrimitiveTypesToTheirObjectPair() throws Exception {
        DefaultParameterResolver defaultParameterResolver = new DefaultParameterResolver();
        defaultParameterResolver.setDefaultConverter(new TVConverter());

        Class notAnnotatedClass = Class.forName(AllPrimitivesParameterBean.class.getName());
        Method method;
        ParameterMetadata metadata;

        method = notAnnotatedClass.getMethod("getByte");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Byte.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("getShort");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Short.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("getInt");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Integer.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("getLong");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Long.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("isBoolean");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Boolean.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("getFloat");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Float.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("getDouble");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Double.class, metadata.getTypeClass());

        method = notAnnotatedClass.getMethod("getChar");
        metadata = defaultParameterResolver.parse(method, null);
        Assert.assertEquals(Character.class, metadata.getTypeClass());
    }

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
        @ParameterComment("This is a long parameter and you can make it even longer")
        public Long getLongParam() {
            return longParam;
        }

        double doubleParam;

        @Parameter(name = "double", defaultValue = "10.5")
        public double getDoubleParam() {
            return doubleParam;
        }

        String optionalStringParam;

        @ParameterValidation(optional = true)
        public String getOptionalStringParam() {
            return optionalStringParam;
        }

        String qualifiedParam;

        @SuppressWarnings("unused")
        @ParameterValidation(format = Formats.XML)
        public String getQualifiedParam(@ParameterQualifier String qualifier) {
            return qualifiedParam;
        }

        @Parameter(converter = TVConverter.class, defaultValue = "true", name = "differentName")
        public Boolean isValue() {
            return false;
        }

        @SuppressWarnings("unused")
        @ParameterValidation(optional = true)
        public void setValue(Boolean value) {

        }

        String xmlString;

        @Parameter(converter = JSONConverter.class)
        @ParameterValidation(format = Formats.XML, validators = { CustomValidator.class })
        public String getXmlString() {
            return xmlString;
        }

        public void setXmlString(String xmlString) {
            this.xmlString = xmlString;
        }
    }

    private class CustomValidator implements ParameterValidator {
        @Override
        public void validate(ParameterMetadata metadata, Object value, Object parameterObject) throws ParameterValidationException {

        }
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testResolveWithoutAnnotationInfo() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Class notAnnotatedClass = Class.forName(NotAnnotatedParameterBean.class.getName());
        // getter
        Method notAnnotatedGetLongMethod = notAnnotatedClass.getMethod("getLongParam");
        DefaultParameterResolver defaultParameterResolver = new DefaultParameterResolver();
        defaultParameterResolver.setDefaultConverter(new TVConverter());
        ParameterMetadata metadata = defaultParameterResolver.parse(notAnnotatedGetLongMethod, null);
        Assert.assertEquals(null, metadata.getDefaultValue());
        Assert.assertEquals(Long.class, metadata.getTypeClass());
        Assert.assertEquals(Operation.READ, Operation.valueOfByMethodName(notAnnotatedGetLongMethod.getName()));
        Assert.assertEquals("LongParam", metadata.getParameter());
        Assert.assertEquals("org.ext4spring.parameter.DefaultParameterResolverTest.NotAnnotatedParameterBean", metadata.getDomain());
        Assert.assertFalse(metadata.isReadOnly());
        Assert.assertEquals(DefaultParameterValidator.class, metadata.getValidators().get(0));
        // setter
        Method notAnnotatedSetLongMethod = notAnnotatedClass.getMethod("setLongParam", Long.class);
        metadata = defaultParameterResolver.parse(notAnnotatedSetLongMethod, null);
        Assert.assertEquals(Operation.WRITE, Operation.valueOfByMethodName(notAnnotatedSetLongMethod.getName()));
        Assert.assertEquals("LongParam", metadata.getParameter());
        Assert.assertEquals(Long.class, metadata.getTypeClass());
        Assert.assertEquals(null, metadata.getDefaultValue());
        Assert.assertEquals("org.ext4spring.parameter.DefaultParameterResolverTest.NotAnnotatedParameterBean", metadata.getDomain());
        Assert.assertFalse(metadata.isReadOnly());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testResolveWithAnnotationInfo() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Class annotatedClass = Class.forName(AnnotatedParameterBean.class.getName());
        Method annotatedLongMethod = annotatedClass.getMethod("getLongParam");
        DefaultParameterResolver defaultParameterResolver = new DefaultParameterResolver();
        defaultParameterResolver.setDefaultConverter(new TVConverter());
        ParameterMetadata metadata = defaultParameterResolver.parse(annotatedLongMethod, null);
        Assert.assertEquals(null, metadata.getDefaultValue());
        Assert.assertEquals(Long.class, metadata.getTypeClass());
        Assert.assertEquals("longer", metadata.getParameter());
        // class annotations
        Assert.assertEquals("testDomain", metadata.getDomain());
        Assert.assertEquals(DefaultParameterValidator.class, metadata.getValidators().get(0));
        Assert.assertEquals("This is a long parameter and you can make it even longer", metadata.getComment());

        Method annotatedDoubleMethod = annotatedClass.getMethod("getDoubleParam");
        metadata = defaultParameterResolver.parse(annotatedDoubleMethod, null);
        Assert.assertEquals("10.5", metadata.getDefaultValue());
        Assert.assertEquals(Double.class, metadata.getTypeClass());
        Assert.assertEquals(DefaultParameterValidator.class, metadata.getValidators().get(0));

        Method annotatedOptionalMethod = annotatedClass.getMethod("getOptionalStringParam");
        metadata = defaultParameterResolver.parse(annotatedOptionalMethod, null);
        Assert.assertTrue(metadata.isOptional());
        Assert.assertFalse(metadata.isQualified());
        Assert.assertEquals(String.class, metadata.getTypeClass());
        Assert.assertTrue(metadata.isReadOnly());
        Assert.assertEquals(null, metadata.getFormat());

        Method annotatedMethodWithQualifier = annotatedClass.getMethod("getQualifiedParam", String.class);
        metadata = defaultParameterResolver.parse(annotatedMethodWithQualifier, new Object[] { "q1" });
        Assert.assertEquals("QualifiedParam", metadata.getParameter());
        Assert.assertTrue(metadata.isQualified());
        Assert.assertEquals("q1", metadata.getQualifier());
        Assert.assertEquals("QualifiedParam.q1", metadata.getFullParameterName());
        Assert.assertTrue(metadata.isReadOnly());
        Assert.assertEquals(Formats.XML, metadata.getFormat());
        Assert.assertEquals(TVConverter.class, metadata.getConverter());

        Method setterMethodWithAnnotationsOnGetter = annotatedClass.getMethod("setValue", Boolean.class);
        metadata = defaultParameterResolver.parse(setterMethodWithAnnotationsOnGetter, null);
        Assert.assertTrue(metadata.isOptional());
        Assert.assertEquals(Boolean.class, metadata.getTypeClass());
        Assert.assertEquals(TVConverter.class, metadata.getConverter());
        Assert.assertEquals("true", metadata.getDefaultValue());
        Assert.assertEquals("differentName", metadata.getParameter());
        Assert.assertFalse(metadata.isReadOnly());

        Method xmlStringMethod = annotatedClass.getMethod("getXmlString");
        metadata = defaultParameterResolver.parse(xmlStringMethod, null);
        Assert.assertEquals(Formats.XML, metadata.getFormat());
        Assert.assertEquals(CustomValidator.class, metadata.getValidators().get(0));
        Assert.assertEquals(JSONConverter.class, metadata.getConverter());
    }

}
