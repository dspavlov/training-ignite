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

import java.util.StringJoiner;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 *
 */
public class Client {
    private long id = -1;

    @QuerySqlField
    private String name;

    @QuerySqlField
    private String email;

    //TODO (lab 2) make field visible for queries engine
    //TODO (lab 2) setup index.
    @QuerySqlField(index = true)
    private String phoneNumber;

    private long lastLoginTs;

    public Client(int id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public long id() {
        return id;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return new StringJoiner(", ", Client.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("email='" + email + "'")
            .add("phoneNumber='" + phoneNumber + "'")
            .toString();
    }

    public String phone() {
        return phoneNumber;
    }

    public void phone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void id(long l) {
        this.id = l;
    }

    public String name() {
        return name;
    }

    public long lastLoginTs() {
        return lastLoginTs;
    }

    public void lastLoginTs(long ts) {
        lastLoginTs = ts;
    }
}
