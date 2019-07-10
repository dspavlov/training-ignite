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

import com.google.common.base.Strings;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.training.ignite.model.Client;

/**
 *
 */
public class ClientStorage {
    /** Cache name. */
    public static final String CACHE_NAME = "clients";

    /** Cache instance */
    private final IgniteCache<Long, Client> cache;

    /** Phone util. */
    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public ClientStorage() {
        // TODO (lab1) Get an instance of Ignite cache.
        Ignite ignite = Ignition.ignite();

        // TODO (lab1) Get an instance of named cache.
        this.cache = ignite.cache(CACHE_NAME);

        A.ensure(cache != null, "Cache [" + CACHE_NAME + "] does not exist. " +
            "Please make sure it was configured at client or server node: " + ignite.cacheNames());
    }

    /**
     * @param client Client.
     */
    public void save(Client client) {
        preprocessClient(client);

        // TODO (lab1) Implement putting into cache operation, you can obtain Client id from entity
        cache.put(client.id(), client);
    }

    private String normalizePhoneNumber(String phone) throws NumberParseException {
        Phonenumber.PhoneNumber parsedNum = phoneUtil.parse(phone, "CH");

        return phoneUtil.format(parsedNum, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }

    /**
     * @param key Key.
     */
    public Client load(long key) {
        // TODO (lab1) Implement getting data from cache by key.
        return cache.get(key);
    }

    /**
     * Finds client by phone number. Normalizes pnoe
     * @param phone Phone.
     */
    public Client findClientByPhone(String phone) {
        String phoneNumber;
        try {
            phoneNumber = normalizePhoneNumber(phone);
        }
        catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());

            phoneNumber = phone;
        }

        // TODO (lab 2) Use Query for find client using phoneNumber prepared for query.
        try (QueryCursor<Cache.Entry<Long, Client>> qry
                 = cache.query(
            new SqlQuery<Long, Client>(Client.class, "where phoneNumber = ?")
                .setArgs(phoneNumber))) {

            Iterator<Cache.Entry<Long, Client>> iter = qry.iterator();

            if (iter.hasNext())
                return iter.next().getValue();
        }

        return null;

    }

    /**
     * @param list List of clients to save.
     */
    public void saveAll(List<Client> list) {
        Map<Long, Client> map = new HashMap<>();

        list.forEach(client -> {
            preprocessClient(client);

            map.put(client.id(), client);
        });

        cache.putAll(map);
    }

    /**
     * Runs changes before saving client in DB to provide consistent results in queries.
     *
     * @param client Client.
     */
    private void preprocessClient(Client client) {
        if (client.id() == 0)
            client.id((long)(Math.random() * Long.MAX_VALUE));

        try {
            if (!Strings.isNullOrEmpty(client.phone()))
                client.phone(normalizePhoneNumber(client.phone()));
        }
        catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
    }

    /**
     *
     */
    public long size() {
        return cache.size();
    }
}
