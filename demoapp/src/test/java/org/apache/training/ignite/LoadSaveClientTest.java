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

import org.apache.training.ignite.model.Client;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for query implementation for Lab 2: Find client by phone number
 */
public class LoadSaveClientTest extends AbstractLabIntegrationTest {
    /** Client storage. */
    private ClientStorage clients = new ClientStorage();

    @Test
    public void testSaveLoadClient() {
        Client client = new Client(777200, "Jennifer Stain", "jenny@test.com", "+1-541-754-3010");
        System.out.println("Saving client " + client);
        clients.save(client);

        Client reloadedFromGrid = clients.load(client.id());
        assertNotNull(reloadedFromGrid);
        assertTrue(reloadedFromGrid.phone().contains("754-3010"));
    }
}
