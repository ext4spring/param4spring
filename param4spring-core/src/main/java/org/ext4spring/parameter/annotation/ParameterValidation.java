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
package org.ext4spring.parameter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ext4spring.parameter.validation.DefaultParameterValidator;
import org.ext4spring.parameter.validation.ParameterValidator;

/**
 * Configures validation of a single parameter
 * 
 * @author Peter Borbas
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParameterValidation {

    /**
     * By default if its not in any of the repositories and the method returns null, it throws a ParameterException If
     * its true, null default value is allowed
     */
    boolean optional() default false;

    /**
     * Max value of the param. For Strings its the length, for numbers the max numerical value
     * 
     */
    double[] max() default {};

    /**
     * Min value of the param. For Strings its the length, for numbers the min numerical value
     * 
     */
    double[] min() default {};
    
    /**
     * Usable on text types to define the valid format. Use {@link Format} for predefined formats or use your own specific format strings
     * @return
     */
    String[] format() default {};
    
    /**
     * Custom validator classes. If none specified {@link DefaultParameterValidator} will be used
     * @return
     */
    Class<? extends ParameterValidator>[] validators() default {};
    
    
}
