package org.daemawiki.domain.user.usecase;

import org.daemawiki.domain.user.dto.response.GetUserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetUserUsecase {
    Mono<GetUserResponse> getCurrentUser();
    Flux<GetUserResponse> getUserByGenAndMajorAndClub(Integer gen, String major, String club, String sortBy, Integer sortDirection, Integer page, Integer size);

}
