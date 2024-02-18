package com.example.daemawiki.domain.user.model.mapper;

import com.example.daemawiki.domain.user.dto.GetUserResponse;
import com.example.daemawiki.domain.user.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserMapper {

    public Mono<GetUserResponse> userToGetUserResponse(User user) {
        return Mono.just(GetUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .major(user.getMajor().getMajor())
                .profile(user.getProfile())
                .build());
    }

}
