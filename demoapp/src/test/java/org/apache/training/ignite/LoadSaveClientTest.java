package org.apache.training.ignite;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for query implementation for Lab 2: Find client by phone number
 */
public class LoadSaveClientTest extends AbstractLabIntegrationTest {
    private ClientStorage storage = new ClientStorage();

    @Test
    public void testSaveLoadClient() {
        Client client = new Client(777200, "Jennifer Stain", "jenny@boeing.com", "+1-541-754-3010");
        System.out.println("Saving client " + client);
        storage.save(client);

        Client reloadedFromGrid = storage.load(client.id());
        assertNotNull(reloadedFromGrid);
        assertTrue(reloadedFromGrid.phone().contains("754-3010"));
    }
}
