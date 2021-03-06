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
package org.ext4spring.parameter.model;

/**
 * Specifies the way a repository can handle a parameter domain
 * 
 * @author pborbas
 * 
 */
public enum RepositoryMode {
	NONE, // contains no info and cannot save new
	READ_ONLY, // contains info but cannot save
	WRITE_EXISTING, // contains info and can overwrite existing parameters, but
					// cannot save new ones
	WRITE_ALL; // contains info and can save existing and new parameters
}
