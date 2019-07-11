/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package org.apache.training.ignite.runners;

import java.util.Collections;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

/**
 * Contains common methods for configuring Ignite on server and clients
 */
public class IgniteConfigUtil {
    /**
     * Limits connection with local node only to prevent unexpected clusters building.
     * @param cfg Config.
     */
    static IgniteConfiguration limitConnectionWithLocalhost(IgniteConfiguration cfg) {
        cfg.setDiscoverySpi(
            new TcpDiscoverySpi().setIpFinder(
                new TcpDiscoveryVmIpFinder().setAddresses(Collections.singletonList("127.0.0.1:47500..47503"))));

        return cfg;
    }

    public static IgniteConfiguration commonConfig(IgniteConfiguration cfg) {
        limitConnectionWithLocalhost(cfg);

        //TODO (Lab 4): Enable Zero Deployment feature, will be applied to all runners
        cfg.setPeerClassLoadingEnabled(true);

        return cfg;
    }
}
