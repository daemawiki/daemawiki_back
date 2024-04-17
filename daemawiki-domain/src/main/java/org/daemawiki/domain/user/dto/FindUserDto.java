package org.daemawiki.domain.user.dto;

import org.springframework.data.domain.Pageable;

public record FindUserDto(Integer gen, String major, String club, String orderBy, String sort, Pageable pageable) {

    public static FindUserDto of(Integer gen, String major, String club, String orderBy, String sort, Pageable pageable) {
        return new FindUserDto(gen, major, club, orderBy, sort, pageable);
    }

}
