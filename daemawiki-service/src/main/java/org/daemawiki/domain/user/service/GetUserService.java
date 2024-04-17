package org.daemawiki.domain.user.service;

import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.usecase.GetUserUsecase;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

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
    public Flux<GetUserResponse> getUserByGenAndMajorAndClub(Integer gen, String major, String club, String orderBy, String sort, Integer page, Integer size) {
        return findUserPort.findAllByGenAndMajorAndClub(
                gen, major, club, orderBy, sort,
                PageRequest.of(page, size)
        ).map(GetUserResponse::of);
    }

}
