package org.apache.training.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

public class SingleClientLoader {
    /**
     * @param args Args.
     */
    public static void main(String[] args) {
        CacheConfiguration ccfg = new CacheConfiguration(ClientStorage.CACHE_NAME);
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC)
            .setCacheMode(CacheMode.PARTITIONED);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setCacheConfiguration(ccfg);

        Ignite start = Ignition.start(cfg);

        ClientStorage storage = new ClientStorage();

        int customerKey = 4041;
        Client client = new Client(customerKey, "John Taylor", "johnt@apache.org");

        storage.save(client);

        Client loadedClient = storage.load(customerKey);
        System.out.println("");

        start.close();
    }
}
