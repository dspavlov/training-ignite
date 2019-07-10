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

import java.io.IOException;
import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.training.ignite.AccountStorage;
import org.apache.training.ignite.ClientStorage;

/**
 *
 */
public class StartServerNodeRunner {
    /**
     * @param args Args.
     */
    public static void main(String[] args) throws IOException {
        CacheConfiguration ccfg = ClientStorage.cacheConfig();

        CacheConfiguration acntCfg = AccountStorage.cacheConfig();

        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setCacheConfiguration(ccfg, acntCfg);

        cfg.setConnectorConfiguration(new ConnectorConfiguration());

        // Limiting connection with local node only to prevent unexpected clusters building.
        cfg.setDiscoverySpi(
            new TcpDiscoverySpi().setIpFinder(
                new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("localhost"))));

        try (Ignite ignite = Ignition.start(cfg)) {
            System.out.print("Press any key to stop server.");
            System.in.read();
        }
    }

}
