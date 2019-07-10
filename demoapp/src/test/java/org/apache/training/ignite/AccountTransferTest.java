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

import com.google.common.collect.Lists;
import org.apache.training.ignite.model.Account;
import org.apache.training.ignite.model.Client;
import org.apache.training.ignite.runners.StartServerNodeRunner;
import org.junit.Test;

/**
 * <b>Note:</b> Before running test start server node by running {@link StartServerNodeRunner}.
 */
public class AccountTransferTest extends AbstractLabIntegrationTest {
    /** Storage. */
    private ClientStorage clients = new ClientStorage();

    /** Storage. */
    private AccountStorage accounts = new AccountStorage();

    /** Account. */
    private AccountTransfer transfer = new AccountTransfer();

    @Test
    public void testAcntTransfer() {
        int srcId = 10001;
        Client src = new Client(srcId, "Initiator", "init@test.test", "+7931332112321");
        String phoneNum = "+79217502321";
        int destId = 10002;
        Client dest = new Client(destId, "Destionation", "dest@test.test", phoneNum);

        clients.saveAll(Lists.newArrayList(src, dest));

        Account account = new Account()
            .ownerClientId(srcId)
            .balance(10000);

        transfer.transferToByPhoneNumber(src, account, phoneNum, 9999);

    }
}
