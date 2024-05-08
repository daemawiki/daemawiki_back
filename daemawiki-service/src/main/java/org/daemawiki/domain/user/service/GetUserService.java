package org.daemawiki.domain.user.service;

import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.dto.FindUserDto;
import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.usecase.GetUserUsecase;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetUserService implements GetUserUsecase {
    private final FindUserPort findUserPort;

    public GetUserService(FindUserPort findUserPort) {
        this.findUserPort = findUserPort;
    }

    @Override
    public Mono<GetUserResponse> getCurrentUser() {
        return findUserPort.currentUser()
                .map(GetUserResponse::of);
    }

    @Override
    public Flux<GetUserResponse> getUserByGenAndMajorAndClub(Integer gen, String major, String club, String sortBy, Integer sortDirection, Integer page, Integer size) {
        return findUserPort.findAllByGenAndMajorAndClub(
                FindUserDto.of(gen, major, club, PagingInfo.of(sortBy, sortDirection, page, size)))
                .map(GetUserResponse::of);
    }

}
