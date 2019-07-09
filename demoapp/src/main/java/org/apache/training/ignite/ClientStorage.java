package org.apache.training.ignite;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.internal.util.typedef.internal.A;

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
        try {
            client.phone(normalizePhoneNumber(client.phone()));
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

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

    public Client findClientByPhone(String phone) {
        // TODO (lab 2) Use Query for find client
        String phoneNumber;
        try {
            phoneNumber = normalizePhoneNumber(phone);
        }
        catch (NumberParseException e) {
            e.printStackTrace();
            phoneNumber = phone;
        }

        try (QueryCursor<Cache.Entry<Long, Client>> query
                 = cache.query(new SqlQuery<Long, Client>(Client.class, "where phoneNumber=?"))) {
            for(Cache.Entry<Long, Client> clientEntry: query) {
                return clientEntry.getValue();
            }
        }

        return null;


    }
}
