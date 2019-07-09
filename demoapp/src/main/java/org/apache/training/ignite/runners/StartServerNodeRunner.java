package org.apache.training.ignite.runners;

import java.io.IOException;
import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.training.ignite.ClientStorage;

/**
 *
 */
public class StartServerNodeRunner {
    /**
     * @param args Args.
     */
    public static void main(String[] args) throws IOException {
        CacheConfiguration ccfg = new CacheConfiguration(ClientStorage.CACHE_NAME);

        //TODO (lab 1) Set cache configuration, Atomic and Partitioned
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC)
            .setCacheMode(CacheMode.PARTITIONED);

        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setCacheConfiguration(ccfg);

        // Limiting connection with local node only to prevent unexpected clusters building.
        cfg.setDiscoverySpi(
            new TcpDiscoverySpi().setIpFinder(
                new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("localhost"))));

        try (Ignite ignite = Ignition.start(cfg)) {
            System.out.print("Press any key to stop server.");
            System.in.read();
        }
    }
}
