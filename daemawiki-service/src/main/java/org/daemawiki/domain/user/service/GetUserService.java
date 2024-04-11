package org.daemawiki.domain.user.service;

import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.type.major.component.GetMajorType;
import org.daemawiki.domain.user.usecase.GetUserUsecase;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class GetUserService implements GetUserUsecase {
    private final FindUserPort findUserPort;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final GetMajorType getMajorType;
    private final Scheduler scheduler;

    public GetUserService(FindUserPort findUserPort, GetMajorType getMajorType, ReactiveMongoTemplate reactiveMongoTemplate, Scheduler scheduler) {
        this.findUserPort = findUserPort;
        this.getMajorType = getMajorType;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
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
        return findUserPort.findAllByDetail_MajorOrderByNameAsc(getMajorType.execute(major))
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
        Query query = new Query();

        if(gen != null) {
            query.addCriteria(Criteria.where("detail.gen").is(gen));
        }
        if(major != null && !major.isBlank()) {
            query.addCriteria(Criteria.where("detail.major").is(getMajorType.execute(major.toLowerCase())));
        }
        if(club != null && !club.isBlank()) {
            query.addCriteria(Criteria.where("detail.club").is(club));
        }
        if ((orderBy != null && !orderBy.isBlank())) {
            if (sort != null &&  sort.equalsIgnoreCase("desc")) {
                query.with(Sort.by(Sort.Direction.DESC, orderBy));
            } else {
                query.with(Sort.by(Sort.Direction.ASC, orderBy));
            }
        }

        return reactiveMongoTemplate.find(query, User.class)
                .subscribeOn(scheduler)
                .map(GetUserResponse::of);
    }

}
