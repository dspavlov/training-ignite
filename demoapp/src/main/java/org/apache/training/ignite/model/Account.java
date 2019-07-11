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

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Account {
    private long id = -1;

    @QuerySqlField(index = true)
    private long ownerClientId = -1;

    /** Balance in smallest possible amount of money for a currency, count of cents. */
    private long balance;

    private String currencyCode;

    private long createdTs = System.currentTimeMillis();

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

    public Account balance(long balance) {
        this.balance = balance;

        return this;
    }

    public long id() {
        return id;
    }

    public Account id(long l) {
        id = l;

        return this;
    }

    /**
     *
     */
    public long balance() {
        return balance;
    }

    public String currencyCode() {
        return currencyCode;
    }

    public long createdTs() {
        return createdTs;
    }
}
