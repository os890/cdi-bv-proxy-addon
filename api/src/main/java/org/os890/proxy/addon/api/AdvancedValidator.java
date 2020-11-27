/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.os890.proxy.addon.api;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.os890.proxy.addon.spi.InstanceResolver;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

import static org.apache.deltaspike.core.util.ProxyUtils.getUnproxiedClass;

public class AdvancedValidator {
    public static Validator wrap(final Validator validator) {
        InstanceResolver instanceResolver = BeanProvider.getContextualReference(InstanceResolver.class, false);
        return new Validator() {
            @Override
            public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
                object = instanceResolver.resolveInstance(object);
                return validator.validate(object, groups);
            }

            @Override
            public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
                object = instanceResolver.resolveInstance(object);
                return validator.validateProperty(object, propertyName, groups);
            }

            @Override
            public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
                beanType = getUnproxiedClass(beanType);
                return validator.validateValue(beanType, propertyName, value, groups);
            }

            @Override
            public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
                clazz = getUnproxiedClass(clazz);
                return validator.getConstraintsForClass(clazz);
            }

            @Override
            public <T> T unwrap(Class<T> type) {
                type = getUnproxiedClass(type);
                return validator.unwrap(type);
            }

            @Override
            public ExecutableValidator forExecutables() {
                return validator.forExecutables();
            }
        };
    }
}
