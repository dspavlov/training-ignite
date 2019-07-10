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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.training.ignite.model.Client;
import org.apache.training.ignite.ClientStorage;

/**
 * Stores test data into cache.
 */
public class MassClientLoader {
    /**
     * @param args Args.
     */
    public static void main(String[] args) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // Limiting connection with local node only to prevent unexpected clusters building.
        cfg.setDiscoverySpi(
            new TcpDiscoverySpi().setIpFinder(
                new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("localhost"))));

        cfg.setClientMode(true);

        try (Ignite ignClient = Ignition.start(cfg)) {
            ClientStorage storage = new ClientStorage();

            InputStream stream = MassClientLoader.class.getResourceAsStream("/clients.json");
            A.ensure(stream != null, "Stream with test data was not found");

            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            TypeToken<ArrayList<Client>> typeTok = new TypeToken<ArrayList<Client>>() {
            };

            List<Client> list = new Gson().fromJson(reader, typeTok.getType());

            storage.saveAll(list);

            System.out.println("Clients in storage: " + storage.size());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
