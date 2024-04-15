package org.daemawiki.domain.user.model;

public record Writer(
        String id,
        String name,
        String email
) {

    public static Writer of(User user) {
        return new Writer(user.getId(), user.getName(), user.getEmail());
    }

}
