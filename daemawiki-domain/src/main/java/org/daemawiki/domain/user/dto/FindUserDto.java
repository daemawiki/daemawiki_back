package org.daemawiki.domain.user.dto;

import org.daemawiki.utils.PagingInfo;

public record FindUserDto(Integer gen, String major, String club, PagingInfo pagingInfo) {

    public static FindUserDto of(Integer gen, String major, String club, PagingInfo pagingInfo) {
        return new FindUserDto(gen, major, club, pagingInfo);
    }

}
