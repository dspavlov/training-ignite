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
package org.apache.training.ignite;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.training.ignite.model.Client;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test for query implementation for Lab 2: Find client by phone number
 */
public class ComputeAndEntryProcessorImplementationTest extends AbstractLabIntegrationTest {
    private ClientStorage clients = new ClientStorage();

    @Test
    public void testLoggedInTodayCompute() {
        int firstKey = 1000000;
        for (int i = 0; i < 10; i++) {
            int id = firstKey + i;
            Client client = new Client(id, "Test For Compute " + i, "test" + i + "@test.com",
                "+1-541-754-200" + i);

            if (i != 0)
                client.lastLoginTs(System.currentTimeMillis());
            System.out.println("Saving client " + client);
            clients.save(client);
        }

        int today = clients.clientsCntLoggedInToday();
        assertTrue(today >= 9);

        clients.save(clients.load(firstKey).lastLoginTs(System.currentTimeMillis()));

        int todayUpdated = clients.clientsCntLoggedInToday();
        assertTrue(todayUpdated >= today + 1);
    }

    @Test
    public void testEntryProcessor() {
        List<Client> clientsToSave = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int id = 2000000 + i;
            Client client = new Client(id, "Test For EntryProc " + i, "entryproc" + i + "@test.com",
                "+1-541-754-100" + i);

            clientsToSave.add(client);
        }

        Set<Long> ids = clients.saveAll(clientsToSave);

        int notified = clients.sendMessage(2, ids, "Join Apache Ignite Project",
            " Visit https://ignite.apache.org/community/resources.html for more deatail, ");

        assertTrue("Expected to notify at least 10 customers", notified >= 10);

        Client load = clients.load(ids.iterator().next());
        
        assertTrue(!load.messages().isEmpty());
    }

}
