package org.apache.training.ignite;

import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractLabIntegrationTest {
    private static Ignite ignite;

    @BeforeClass
    public static void connectToIgnite() {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // Limiting connection with local node only to prevent unexpected clusters building.
        cfg.setDiscoverySpi(
            new TcpDiscoverySpi().setIpFinder(
                new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("localhost"))));

        cfg.setClientMode(true);

        ignite = Ignition.start(cfg);
    }

    @AfterClass
    public static void closeClient() {
        ignite.close();
    }
}
