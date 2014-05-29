package org.ext4spring.parameter.converter;

import org.ext4spring.parameter.converter.tv.TVConverter;

public class TVConverterTest extends AbstractConverterTest {
    private Converter converter = new TVConverter();

    @Override
    protected Converter getConverter() {
        return converter;
    }

}
