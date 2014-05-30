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

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.ext4spring.parameter.converter.ConverterFactory;
import org.ext4spring.parameter.converter.tv.TVConverter;
import org.ext4spring.parameter.dao.ParameterRepository;
import org.ext4spring.parameter.exception.ParameterException;
import org.ext4spring.parameter.exception.ParameterUndefinedException;
import org.ext4spring.parameter.exception.RepositoryNotFoundException;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.ext4spring.parameter.model.RepositoryMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class DefaultParameterServiceTest extends TestBase {

    ConverterFactory mockConverterFactory = Mockito.mock(ConverterFactory.class);

    ParameterRepository repoNone = this.createMockRepository(false, RepositoryMode.NONE);
    ParameterRepository repoReadNotExists = this.createMockRepository(false, RepositoryMode.READ_ONLY);
    ParameterRepository repoReadExists = this.createMockRepository(true, RepositoryMode.READ_ONLY);
    ParameterRepository repoWriteExNotExists = this.createMockRepository(false, RepositoryMode.WRITE_EXISTING);
    ParameterRepository repoWriteExExists = this.createMockRepository(true, RepositoryMode.WRITE_EXISTING);
    ParameterRepository repoWriteAllNotExist = this.createMockRepository(false, RepositoryMode.WRITE_ALL);
    ParameterRepository repoWriteAllExist = this.createMockRepository(true, RepositoryMode.WRITE_ALL);
    LinkedHashSet<ParameterRepository> mockRepos;

    @Before
    public void init() {
        Mockito.when(mockConverterFactory.get(TVConverter.class)).thenReturn(new TVConverter());

    }

    @Test
    public void testGetExistingParameter() {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getValue(Matchers.any(ParameterMetadata.class))).thenReturn("asd");
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(true);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(RepositoryMode.WRITE_ALL);
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();
        repos.add(mockParameterRepository);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setConverter(TVConverter.class);

        Assert.assertEquals("asd", parameterService.read(metadata, null));
    }

    @Test
    public void testGetExistingNullParameter() {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getValue(Matchers.any(ParameterMetadata.class))).thenReturn(null);
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(true);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(RepositoryMode.WRITE_ALL);
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();
        repos.add(mockParameterRepository);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setOptional(true);
        metadata.setTypeClass(String.class);

        Assert.assertEquals(null, parameterService.read(metadata, null));
    }

    @Test
    public void testGetNotExistingParameter() {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getValue(Matchers.any(ParameterMetadata.class))).thenReturn(null);
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(false);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(RepositoryMode.WRITE_ALL);
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();
        repos.add(mockParameterRepository);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);

        try {
            parameterService.read(metadata, null);
            Assert.assertTrue("Should throw exception",false);
        } catch (ParameterUndefinedException e) {
            //op
        }
    }

    @Test
    public void testGetNotExistingParametersDefaultValue() {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getValue(Matchers.any(ParameterMetadata.class))).thenReturn(null);
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(false);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(RepositoryMode.WRITE_ALL);
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();
        repos.add(mockParameterRepository);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setDefaultValue("defaultValue");
        metadata.setConverter(TVConverter.class);

        Assert.assertEquals("defaultValue", parameterService.read(metadata, null));
    }

    @Test
    public void testGetNotExistingParametersMethodReturnValue() {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getValue(Matchers.any(ParameterMetadata.class))).thenReturn(null);
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(false);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(RepositoryMode.WRITE_ALL);
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();
        repos.add(mockParameterRepository);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setDefaultValue("");
        metadata.setConverter(TVConverter.class);

        Assert.assertEquals("defaultValue", parameterService.read(metadata, "defaultValue"));
    }

    @Test
    public void testNotExistingRepositoryGet() {
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setDefaultValue(null);
        metadata.setConverter(TVConverter.class);

        try {
            parameterService.read(metadata, "defaultValue");
            Assert.assertTrue("Exception should thrown", false);
        } catch (RepositoryNotFoundException e) {
        }

    }

    @Test
    public void testNotExistingRepositorySet() {
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setDefaultValue(null);
        metadata.setConverter(TVConverter.class);

        try {
            parameterService.write(metadata, "value", null);
            Assert.assertTrue("Exception should thrown", false);
        } catch (RepositoryNotFoundException e) {
        }
    }

    @Test
    public void testSetParameter() {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getValue(Matchers.any(ParameterMetadata.class))).thenReturn("asd");
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(true);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(RepositoryMode.WRITE_ALL);
        LinkedHashSet<ParameterRepository> repos = new LinkedHashSet<ParameterRepository>();
        repos.add(mockParameterRepository);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);
        parameterService.setParameterRepositories(repos);

        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setConverter(TVConverter.class);

        parameterService.write(metadata, "value", null);
        Mockito.verify(mockParameterRepository).setValue(metadata, "value");
    }

    private ParameterRepository createMockRepository(boolean paramExists, RepositoryMode repositoryMode) {
        ParameterRepository mockParameterRepository = Mockito.mock(ParameterRepository.class);
        Mockito.when(mockParameterRepository.getMode(Matchers.anyString())).thenReturn(repositoryMode);
        Mockito.when(mockParameterRepository.parameterExists(Matchers.any(ParameterMetadata.class))).thenReturn(paramExists);
        return mockParameterRepository;
    }

    @Test
    public void testSelectReadRepository() {
        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setConverter(TVConverter.class);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);

        ParameterRepository[] testedRepositoryOrder;

        testedRepositoryOrder = new ParameterRepository[] { repoNone, repoReadNotExists, repoReadExists, repoWriteExNotExists, repoWriteAllNotExist, repoWriteExExists, repoWriteAllExist };
        parameterService.setParameterRepositories(new LinkedHashSet<ParameterRepository>(Arrays.asList(testedRepositoryOrder)));
        ParameterRepository repositoryFound = parameterService.getReadableRepositoryWhereParameterExists(metadata);
        Assert.assertEquals(repoReadExists, repositoryFound);

        testedRepositoryOrder = new ParameterRepository[] { repoNone, repoReadNotExists, repoWriteExExists };
        parameterService.setParameterRepositories(new LinkedHashSet<ParameterRepository>(Arrays.asList(testedRepositoryOrder)));
        repositoryFound = parameterService.getReadableRepositoryWhereParameterExists(metadata);
        Assert.assertEquals(repoWriteExExists, repositoryFound);

    }

    @Test
    public void testSelectWriteRepository() {
        ParameterMetadata metadata = new ParameterMetadata();
        metadata.setDomain("domain");
        metadata.setParameter("parameter");
        metadata.setTypeClass(String.class);
        metadata.setConverter(TVConverter.class);

        DefaultParameterService parameterService = new DefaultParameterService();
        parameterService.setConverterFactory(mockConverterFactory);

        ParameterRepository[] testedRepositoryOrder;

        testedRepositoryOrder = new ParameterRepository[] { repoNone, repoReadNotExists, repoWriteExNotExists, repoWriteAllNotExist, repoWriteExExists, repoWriteAllExist };
        parameterService.setParameterRepositories(new LinkedHashSet<ParameterRepository>(Arrays.asList(testedRepositoryOrder)));
        ParameterRepository repositoryFound = parameterService.getWriteableRepository(metadata);
        Assert.assertEquals(repoWriteAllNotExist, repositoryFound);

        testedRepositoryOrder = new ParameterRepository[] { repoNone, repoReadNotExists, repoReadExists, repoWriteExNotExists, repoWriteExExists, repoWriteAllNotExist, repoWriteAllExist };
        parameterService.setParameterRepositories(new LinkedHashSet<ParameterRepository>(Arrays.asList(testedRepositoryOrder)));
        try {
            repositoryFound = parameterService.getWriteableRepository(metadata);
            Assert.assertTrue("Exception should thrown becase parameter was read from a read only repo", false);
        } catch (ParameterException e) {
            //ok
        }

    }

}
