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

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.training.ignite.ClientStorage;
import org.apache.training.ignite.model.Client;

public class SingleClientLoader {
    /**
     * @param args Args.
     */
    public static void main(String[] args) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        IgniteConfigUtil.commonConfig(cfg);

        cfg.setClientMode(true);

        try (Ignite ignClient = Ignition.start(cfg)) {
            ClientStorage storage = new ClientStorage();
            int customerKey = 4041;
            Client client = new Client(customerKey,
                "John Taylor",
                "johnt@apache.org",
                "044 668 18 00");

            storage.save(client);

            Client loadedClient = storage.load(customerKey);
            System.out.println("Client data " + loadedClient);

            // you can also check if client is available via REST API:
            // http://localhost:8080/ignite?cmd=get&key=4041&cacheName=clients&keyType=long
            // http://localhost:8080/ignite?cmd=qryscanexe&pageSize=100&cacheName=clients&keyType=long

            Client cliReloaded = storage.findClientByPhone(client.phone());
            System.out.println("Client found by phone " + cliReloaded);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
