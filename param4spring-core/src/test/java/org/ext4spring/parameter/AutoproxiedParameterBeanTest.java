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

import org.ext4spring.parameter.example.ApplicationSettings;
import org.ext4spring.parameter.exception.ValueOutOfBoundsValidationException;
import org.ext4spring.parameter.model.ParameterBeanMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testAutoproxiedHsqlContext.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class AutoproxiedParameterBeanTest extends TestBase {

    @Autowired
    ApplicationSettings applicationSettings;

    @Autowired
    ParameterBeanService parameterBeanService;

    @Test
    public void testPropertyValuesReadByParameterAspect() throws InterruptedException {
        TestUtil.assertApplicationSettingsValid(this.applicationSettings);
        TestUtil.assertQualifiedApplicationSettingsValid(applicationSettings);
    }

    @Test
    public void testModifyAutoproxiedProperty() {
        applicationSettings.setName("new name");
        Assert.assertEquals("new name", applicationSettings.getName());
    }

    @Test(expected = ValueOutOfBoundsValidationException.class)
    public void testModifyAutoproxiedPropertyIsValidated() {
        applicationSettings.setPrice(50);
    }

    @Test
    public void testParameterBeanAutomaticallyRegistered() {
        List<ParameterBeanMetadata> parameterBeanMetadatas = this.parameterBeanService.listParameterBeans();
        Assert.assertEquals(1, parameterBeanMetadatas.size());
        Assert.assertEquals(ApplicationSettings.class, parameterBeanMetadatas.get(0).getParameterBeanClass());
    }
}
