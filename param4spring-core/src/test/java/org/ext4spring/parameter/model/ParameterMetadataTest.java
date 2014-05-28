package org.ext4spring.parameter.model;

import org.junit.Assert;
import org.junit.Test;

public class ParameterMetadataTest {

    @Test
    public void testFullNameParser() {
        Assert.assertEquals("qualifier", ParameterMetadata.parseQualifier(ParameterMetadata.createFullName("parameter", "qualifier"), "parameter"));
    }
}
