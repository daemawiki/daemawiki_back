package org.daemawiki.domain.user.service;

import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.model.type.major.MajorType;
import org.daemawiki.domain.user.usecase.GetUserUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class GetUserService implements GetUserUsecase {
    private final FindUserPort findUserPort;
    private final Scheduler scheduler;

    public GetUserService(FindUserPort findUserPort, Scheduler scheduler) {
        this.findUserPort = findUserPort;
        this.scheduler = scheduler;
    }

    @Override
    public Flux<GetUserResponse> getUserByGen(Integer gen) {
        return findUserPort.findAllByDetail_GenOrderByNameAsc(gen)
                .subscribeOn(scheduler)
                .map(GetUserResponse::of);
    }

    @Override
    public Flux<GetUserResponse> getUserByMajor(String major) {
        return findUserPort.findAllByDetail_MajorOrderByNameAsc(MajorType.valueOf(major))
                .subscribeOn(scheduler)
                .map(GetUserResponse::of);
    }

    @Override
    public Flux<GetUserResponse> getUserByClub(String club) {
        return findUserPort.findAllByDetail_ClubOrderByNameAsc(club)
                .subscribeOn(scheduler)
                .map(GetUserResponse::of);
    }

    @Override
    public Mono<GetUserResponse> getCurrentUser() {
        return findUserPort.currentUser()
                .map(GetUserResponse::of);
    }

    @Override
    public Flux<GetUserResponse> getUserByGenAndMajorAndClub(Integer gen, String major, String club, String orderBy, String sort) {
        return findUserPort.findAllByGenAndMajorAndClub(
                gen, major, club, orderBy, sort
        ).map(GetUserResponse::of);
    }

}
