package org.apache.training.ignite;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for query implementation for Lab 2: Find client by phone number
 */
public class FindClientByPhoneTest extends AbstractLabIntegrationTest {
    /** Storage. */
    private ClientStorage storage = new ClientStorage();

    @Test
    public void testSearchClientByPhone() {
        String phoneNum = "+79173274107";
        Client client = new Client(7007, "Ivan Ivanov", "ivan@ivanov.ru", phoneNum);
        System.out.println("Saving client " + client);
        storage.save(client);

        Client foundByPhone = storage.findClientByPhone(phoneNum);
        System.out.println("Client found " + foundByPhone);

        assertNotNull(foundByPhone);
        assertTrue(foundByPhone.phone().contains("917"));
    }
}
