package org.apache.training.ignite.runners;

import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.training.ignite.Client;
import org.apache.training.ignite.ClientStorage;

public class SingleClientLoader {
    /**
     * @param args Args.
     */
    public static void main(String[] args) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        // Limiting connection with local node only to prevent unexpected clusters building.
        cfg.setDiscoverySpi(
            new TcpDiscoverySpi().setIpFinder(
                new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("localhost"))));

        cfg.setClientMode(true);

        try (Ignite ignClient = Ignition.start(cfg)) {
            ClientStorage storage = new ClientStorage();
            int customerKey = 4041;
            Client client = new Client(customerKey,
                "John Taylor",
                "johnt@apache.org",
                "044 668 18 00");

            storage.save(client);

            Client loadedClient = storage.load(customerKey);
            System.out.println("Client data " + loadedClient);

            // you can also check if client is available via REST API:
            // http://localhost:8080/ignite?cmd=get&key=4041&cacheName=clients&keyType=long
            // http://localhost:8080/ignite?cmd=qryscanexe&pageSize=100&cacheName=clients&keyType=long
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
