package org.daemawiki.domain.user.mapper;

import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public Mono<GetUserResponse> userToGetUserResponse(User user) {
        return Mono.just(GetUserResponse.builder()
                .userId(user.getId())
                .documentId(user.getDocumentId())
                .name(user.getName())
                .detail(user.getDetail())
                .profile(user.getProfile())
                .build());
    }

}
