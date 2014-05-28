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
package org.ext4spring.parameter.example;

import java.util.Date;

import org.ext4spring.parameter.annotation.Parameter;
import org.ext4spring.parameter.annotation.ParameterBean;
import org.ext4spring.parameter.annotation.ParameterQualifier;
import org.ext4spring.parameter.converter.tv.TVConverter;

/**
 * Example configuration POJO
 * 
 * @author Peter Borbas
 */
@ParameterBean(domain = "applicationSettings")
public class ApplicationSettings {

    /**
     * For unit test validation. The final values after reading from parameter repository
     */
    public static final boolean SUPPORTED = false;
    public static final String NAME = "Application name";
    public static final long SIZE = 123;
    private static final String PRICE_STRING = "10.56";
    public static final Double PRICE = Double.valueOf(PRICE_STRING);
    public static final Date RELEAS_DATE = new Date(1000);
    public static final String XML_DATA="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String USER_COLOR_DEFAULT="black";
    public static final String USER_COLOR_USER1="blue";
    public static final String USER_COLOR_USER2="red";
    public static final Boolean USER_ENABLED_USER1=true;
    public static final Boolean USER_ENABLED_USER2=false;

    private boolean supported;
    private String name;
    private String xmlConfig;
    private long size = SIZE;
    private double price;
    private Date releaseDate;
    private String userColor="black";
    private Boolean userEnabled=false;

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Parameter(name = "Price", defaultValue = PRICE_STRING)
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Parameter(converter = TVConverter.class, optional=true)
    public String getXmlConfig() {
        return xmlConfig;
    }

    public void setXmlConfig(String xmlConfig) {
        this.xmlConfig = xmlConfig;
    }
    
    /**
     * Parameter with qualifier but map values as bean attribute
     * @param userName
     * @return
     */
    @Parameter(converter=TVConverter.class)
    public String getUserColor(@ParameterQualifier String userName){
        return this.userColor;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }
    /**
     * Parameter with qualifier but single value as bean attribute
     * @param userName
     * @return
     */
    public Boolean isUserEnabled(@ParameterQualifier String userName) {
        return userEnabled;
    }
    
}
