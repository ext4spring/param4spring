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

import java.util.ArrayList;
import java.util.List;

import org.ext4spring.parameter.dao.ParameterRepository;
import org.ext4spring.parameter.model.ParameterMetadata;
import org.ext4spring.parameter.model.RepositoryMode;

public class MockParameterRepository implements ParameterRepository {

	private String value = "initialValue";
	public static int getCounter;
	public static int setCounter;

	public static void reset() {
		getCounter = 0;
		setCounter = 0;
	}

	@Override
	public boolean parameterExists(ParameterMetadata metadata) {
		return true;
	}

	@Override
	public String getValue(ParameterMetadata metadata) {
		getCounter++;
		return value;
	}

	@Override
	public void setValue(ParameterMetadata metadata, String value) {
		setCounter++;
		this.value = value;
	}

	@Override
	public RepositoryMode getMode(String domain) {
		return RepositoryMode.WRITE_ALL;
	}
	
	@Override
	public List<String> getParameterQualifiers(ParameterMetadata metadata) {
	    return new ArrayList<String>();
	}

}
