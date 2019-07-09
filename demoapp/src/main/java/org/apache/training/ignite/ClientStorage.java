package org.apache.training.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

/**
 *
 */
public class ClientStorage {
    /** Cache name. */
    public static final String CACHE_NAME = "clients";

    /** Cache instance */
    private final IgniteCache<Long, Client> cache;

    public ClientStorage() {
        // TODO Get an instance of Ignite cache.
        Ignite ignite = Ignition.ignite();

        // TODO Get an instance of named cache.
        this.cache = ignite.cache(CACHE_NAME);
    }

    /**
     * @param client Client.
     */
    public void save(Client client) {
        // TODO Implement putting into cache operation, you can obtain Client id from entity
        cache.put(client.id(), client);
    }

    /**
     * @param key Key.
     */
    public Client load(long key) {
        // TODO Implement getting data from cache by key.=
        return cache.get(key);
    }
}
