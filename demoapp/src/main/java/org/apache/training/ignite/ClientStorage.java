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
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.training.ignite.model.Client;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ClientStorage {
    /** Cache name. */
    public static final String CACHE_NAME = "clients";

    /** Cache instance */
    private final IgniteCache<Long, Client> cache;
    private final Ignite ignite;

    /** Phone util. */
    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public ClientStorage() {
        ignite = Ignition.ignite();

        this.cache = ignite.cache(CACHE_NAME);

        A.ensure(cache != null, "Cache [" + CACHE_NAME + "] does not exist. " +
            "Please make sure it was configured at client or server node: " + ignite.cacheNames());
    }

    /**
     * @return Cache configuration for storing clients.
     */
    @NotNull public static CacheConfiguration cacheConfig() {
        CacheConfiguration ccfg = new CacheConfiguration(CACHE_NAME);

        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC)
            .setCacheMode(CacheMode.PARTITIONED)
            .setBackups(1);

        ccfg.setQueryEntities(Collections.singletonList(new QueryEntity(Long.class, Client.class)));
        return ccfg;
    }

    /**
     * @param client Client.
     */
    public void save(Client client) {
        preprocessClient(client);

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
        return cache.get(key);
    }

    /**
     * Finds client by phone number. Normalizes pnoe
     * @param phone Phone.
     */
    public Client findClientByPhone(String phone) {
        String phoneNum;
        try {
            phoneNum = normalizePhoneNumber(phone);
        }
        catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());

            phoneNum = phone;
        }

        try (QueryCursor<Cache.Entry<Long, Client>> qry
                 = cache.query(
            new SqlQuery<Long, Client>(Client.class, "where phoneNumber = ?")
                .setArgs(phoneNum))) {

            Iterator<Cache.Entry<Long, Client>> iter = qry.iterator();

            if (iter.hasNext())
                return iter.next().getValue();
        }

        return null;

    }

    /**
     * @param list List of clients to save.
     */
    public Set<Long> saveAll(List<Client> list) {
        Map<Long, Client> map = new HashMap<>();

        list.forEach(client -> {
            preprocessClient(client);

            map.put(client.id(), client);
        });

        cache.putAll(map);

        return map.keySet();
    }

    /**
     * Runs changes before saving client in DB to provide consistent results in queries.
     *
     * @param client Client.
     */
    private void preprocessClient(Client client) {
        if (client.id() <= 0)
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

    /**
     * @return Number of customers used this system today.
     */
    public int clientsCntLoggedInToday() {
        ClusterGroup clusterGrp = ignite.cluster().forServers();

        IgniteCompute compute = ignite.compute(clusterGrp);

        IgniteCallable<Integer> call = new IgniteCallable<Integer>() {
            @IgniteInstanceResource
            Ignite ignite;

            @Override public Integer call() {
                IgniteCache<Long, Client> cache = ignite.cache(CACHE_NAME);
                Iterable<Cache.Entry<Long, Client>> entries = cache.localEntries(CachePeekMode.PRIMARY);

                long tsBorder = System.currentTimeMillis() - Duration.ofDays(1).toMillis();
                int loginToday = 0;
                for (Cache.Entry<Long, Client> next : entries) {

                    long lastLogin = next.getValue().lastLoginTs();

                    if (lastLogin != 0 && lastLogin > tsBorder)
                        loginToday++;
                }

                System.out.println("Collecting: client logged in today count: " + loginToday
                    + " at " + ignite.cluster().localNode().id());

                return loginToday;
            }
        };

        Collection<Integer> res = compute.broadcast(call);

        return res.stream().mapToInt(i -> i).sum();
    }

    /**
     * @param msgTypeId Message type id.
     * @param customerIds Customer ids.
     * @param subj Subj.
     * @param msg Message.
     * @return Count of messages sent.
     */
    public int sendMessage(int msgTypeId, Set<Long> customerIds, String subj, String msg) {
        Map<Long, EntryProcessorResult<Boolean>> results;

        results = cache.invokeAll(customerIds, new EntryProcessor<Long, Client, Boolean>() {
            @Override public Boolean process(MutableEntry<Long, Client> entry,
                Object... arguments) throws EntryProcessorException {

                Client client = entry.getValue();
                if (client == null)
                    return false;

                boolean snd = client.sendMessageIfAbsent(msgTypeId, subj, msg);
                entry.setValue(client);

                return snd;
            }
        }, subj, msg);

        return results.values().stream().mapToInt(result -> Boolean.TRUE.equals(result.get()) ? 1 : 0).sum();
    }
}
