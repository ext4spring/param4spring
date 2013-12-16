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
package org.ext4spring.parameter.exception;

@SuppressWarnings("serial")
public class RepositoryNotFoundException extends ParameterException {

	public RepositoryNotFoundException() {
		super();
	}

	public RepositoryNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryNotFoundException(String message) {
		super(message);
	}

	public RepositoryNotFoundException(Throwable cause) {
		super(cause);
	}

}
