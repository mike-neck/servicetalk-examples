/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.spring.bridge.api.SpringBridge;
import org.jvnet.hk2.spring.bridge.api.SpringIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import javax.inject.Inject;

public class JaxRsApplication extends ResourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(JaxRsApplication.class);

    static BeanFactory beanFactory;

    @Inject
    public JaxRsApplication(ServiceLocator serviceLocator) {
        logger.info("initializing resource-config: {}", serviceLocator.getName());

        SpringBridge.getSpringBridge().initializeSpringBridge(serviceLocator);
        SpringIntoHK2Bridge springIntoHK2Bridge = serviceLocator.getService(SpringIntoHK2Bridge.class);
        springIntoHK2Bridge.bridgeSpringBeanFactory(beanFactory);

        registerClasses(TimeResource.class);
    }
}
