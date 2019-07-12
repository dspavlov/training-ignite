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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.training.ignite.model.Account;
import org.jetbrains.annotations.NotNull;

public class AccountStorage {
    /** Cache name. */
    public static final String CACHE_NAME = "accounts";

    /** Cache instance */
    private final IgniteCache<Long, Account> cache;

    public AccountStorage() {
        Ignite ignite = Ignition.ignite();

        this.cache = ignite.cache(CACHE_NAME);

        A.ensure(cache != null, "Cache [" + CACHE_NAME + "] does not exist. " +
            "Please make sure it was configured at client or server node: " + ignite.cacheNames());
    }

    @NotNull public static CacheConfiguration cacheConfig() {
        CacheConfiguration acntCfg = new CacheConfiguration(CACHE_NAME);

        acntCfg.setCacheMode(CacheMode.PARTITIONED)
            .setBackups(1)
            .setQueryEntities(Collections.singletonList(new QueryEntity(Long.class, Account.class)));

        return acntCfg;
    }

    /**
     * @param key Key.
     */
    public Account load(long key) {
        return cache.get(key);
    }

    /**
     * @param acnt Account to save.
     */
    public void save(Account acnt) {
        preprocessAccount(acnt);

        cache.put(acnt.id(), acnt);
    }

    /**
     * @param list List.
     */
    public void saveAll(List<Account> list) {
        Map<Long, Account> map = new HashMap<>();

        list.forEach(account -> {
            preprocessAccount(account);

            map.put(account.id(), account);
        });

        cache.putAll(map);
    }

    private void preprocessAccount(Account account) {
        A.ensure(account.ownerClientId() >= 0, "Account has no Client Owner ID");

        if (account.id() <= 0)
            account.id((long)(Math.random() * Long.MAX_VALUE));
    }

    public List<Account> findAccounts(long cardholder) {
        List<Account> res = new ArrayList<>();
        try (QueryCursor<Cache.Entry<Long, Account>> qry = cache.query(new SqlQuery<Long, Account>(Account.class, "ownerClientId = ?").setArgs(cardholder));) {
            for (Cache.Entry<Long, Account> next : qry)
                res.add(next.getValue());
        }

        return res;

    }
}
