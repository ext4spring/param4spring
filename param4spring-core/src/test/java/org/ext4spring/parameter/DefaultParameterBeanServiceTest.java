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

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.ext4spring.parameter.example.ApplicationSettings;
import org.ext4spring.parameter.model.ParameterBeanMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testNoAOPContext.xml")
@Transactional
public class DefaultParameterBeanServiceTest extends TestBase {

    @Autowired
    ParameterBeanService parameterBeanService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testPropertyValuesReadByParameterBeanService() {
        ApplicationSettings applicationSettings = this.parameterBeanService.readParameterBean(ApplicationSettings.class);
        TestUtil.assertApplicationSettingsValid(applicationSettings);
        Assert.assertEquals("black", applicationSettings.getUserColor(null));
        applicationSettings = this.parameterBeanService.readParameterBean(ApplicationSettings.class, "user1");
        TestUtil.assertApplicationSettingsValid(applicationSettings);
        Assert.assertEquals("blue", applicationSettings.getUserColor("user1"));
        applicationSettings = this.parameterBeanService.readParameterBean(ApplicationSettings.class, "user2");
        TestUtil.assertApplicationSettingsValid(applicationSettings);
        Assert.assertEquals("red", applicationSettings.getUserColor("user2"));
    }

    @Test
    public void testPropertyValuesWriteByParameterBeanService() {
        ApplicationSettings applicationSettings = this.parameterBeanService.readParameterBean(ApplicationSettings.class);
        applicationSettings.setName("Changed name");
        this.parameterBeanService.writeParameterBean(applicationSettings);
        Assert.assertEquals(1, jdbcTemplate.queryForInt("Select count(*) from parameters where data='\"Changed name\"'"));
    }
    
    @Test
    public void testListManuallyRegisteredParameterBeanMetadatas() {
        this.parameterBeanService.register(ApplicationSettings.class);
        List<ParameterBeanMetadata> parameterBeanMetadatas = this.parameterBeanService.listParameterBeans();
        Assert.assertEquals(1, parameterBeanMetadatas.size());
        Assert.assertEquals(ApplicationSettings.class, parameterBeanMetadatas.get(0).getParameterBeanClass());
    }

    @Test
    public void testListIndirectlyRegisteredParameterBeanMetadatas() {
        this.parameterBeanService.readParameterBean(ApplicationSettings.class);
        List<ParameterBeanMetadata> parameterBeanMetadatas = this.parameterBeanService.listParameterBeans();
        Assert.assertEquals(1, parameterBeanMetadatas.size());
        Assert.assertEquals(ApplicationSettings.class, parameterBeanMetadatas.get(0).getParameterBeanClass());
    }
    
    @Test
    public void testReadQualifiersOfParameter() {
        Set<String> qualifiers=this.parameterBeanService.getQualifiers(ApplicationSettings.class);
        Assert.assertEquals(2, qualifiers.size());
        Assert.assertTrue(qualifiers.contains("user1"));
        Assert.assertTrue(qualifiers.contains("user2"));
    }
    
    @Test
    public void testReadBeanWithoutQualifier() {
        ApplicationSettings applicationSettings=this.parameterBeanService.readParameterBean(ApplicationSettings.class);
        TestUtil.assertApplicationSettingsValid(applicationSettings);

    }
    
    @Test
    public void testReadBeanWithQualifier() {
        ApplicationSettings applicationSettings=this.parameterBeanService.readParameterBean(ApplicationSettings.class, "user1");
        TestUtil.assertApplicationSettingsValid(applicationSettings);
        Assert.assertEquals("blue", applicationSettings.getUserColor("user1"));
    }
    
    @Test
    public void saveQualifiedBeanWithExistingQualifier() {
        ApplicationSettings applicationSettings=this.parameterBeanService.readParameterBean(ApplicationSettings.class, "user1");
        applicationSettings.setUserColor("purple");
        this.parameterBeanService.writeParameterBean(applicationSettings, "user1");
        applicationSettings=this.parameterBeanService.readParameterBean(ApplicationSettings.class, "user1");
        Assert.assertEquals("purple", applicationSettings.getUserColor("user1"));
        applicationSettings=this.parameterBeanService.readParameterBean(ApplicationSettings.class);
        Assert.assertEquals("black", applicationSettings.getUserColor(null));
    }
}
