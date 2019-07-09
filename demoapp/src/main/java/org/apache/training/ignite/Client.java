package org.apache.training.ignite;

import java.util.StringJoiner;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 *
 */
public class Client {
    private long id;

    @QuerySqlField
    private String name;

    private String email;

    //TODO (lab 2) make field visible for queries engine
    @QuerySqlField
    private String phoneNumber;

    public Client(int id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public long id() {
        return id;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return new StringJoiner(", ", Client.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("email='" + email + "'")
            .add("phoneNumber='" + phoneNumber + "'")
            .toString();
    }

    public String phone() {
        return phoneNumber;
    }

    public void phone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void id(long l) {
        this.id = l;
    }
}
