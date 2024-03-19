package org.daemawiki.domain.user.mapper;

import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Mono;

public interface UserMapper {
    Mono<GetUserResponse> userToGetUserResponse(User user);

}
