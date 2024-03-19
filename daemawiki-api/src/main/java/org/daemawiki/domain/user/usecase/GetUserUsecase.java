package org.daemawiki.domain.user.usecase;

import org.daemawiki.domain.user.dto.response.GetUserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetUserUsecase {
    Flux<GetUserResponse> getUserByGen(Integer gen);
    Flux<GetUserResponse> getUserByMajor(String major);
    Flux<GetUserResponse> getUserByClub(String club);
    Mono<GetUserResponse> getCurrentUser();
    Flux<GetUserResponse> getUserByGenAndMajorAndClub(Integer gen, String major, String club, String orderBy, String sort);

}
