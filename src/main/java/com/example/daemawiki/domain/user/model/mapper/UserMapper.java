package com.example.daemawiki.domain.user.model.mapper;

import com.example.daemawiki.domain.user.dto.response.GetUserResponse;
import com.example.daemawiki.domain.user.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserMapper {

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
