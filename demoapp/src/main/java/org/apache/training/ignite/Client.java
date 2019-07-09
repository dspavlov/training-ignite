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

    public Client(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public long id() {
        return id;
    }

    @Override public String toString() {
        return new StringJoiner(", ", Client.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("email='" + email + "'")
            .toString();
    }
}
