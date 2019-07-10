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
package org.apache.training.ignite.model;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class Account {
    long id;

    @AffinityKeyMapped
    long ownerClientId;

    /** Balance in smallest possible amount of money for a currency, count of cents. */
    long balance;

    String currencyCode;

    /**
     *
     */
    public long ownerClientId() {
        return ownerClientId;
    }

    public Account ownerClientId(int id) {
        this.ownerClientId = id;

        return this;
    }

    public Account balance(int balance) {
        this.balance = balance;

        return this;
    }
}
