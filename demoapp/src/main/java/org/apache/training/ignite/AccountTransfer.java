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

import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.training.ignite.model.Account;
import org.apache.training.ignite.model.Client;

public class AccountTransfer {
    ClientStorage clients = new ClientStorage();
    AccountStorage accounts = new AccountStorage();

    public void transferToByPhoneNumber(Client initiator, Account from, String toPhoneNum, long amount) {
        Client dest = clients.findClientByPhone(toPhoneNum);
        A.ensure(dest != null, "Destination account not found by phone [" + toPhoneNum + "]");

        A.ensure(initiator.id() == from.ownerClientId(), "Initiator is not an owner, power of attorney is not implemented");

    }
}
