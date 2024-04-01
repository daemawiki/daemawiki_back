package org.daemawiki.domain.user.usecase.service;

import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.mapper.UserMapper;
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
    private final GetUserPort getUserPort;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final GetMajorType getMajorType;
    private final Scheduler scheduler;
    private final UserMapper userMapper;

    public GetUserService(GetUserPort getUserPort, GetMajorType getMajorType, ReactiveMongoTemplate reactiveMongoTemplate, Scheduler scheduler, UserMapper userMapper) {
        this.getUserPort = getUserPort;
        this.getMajorType = getMajorType;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.scheduler = scheduler;
        this.userMapper = userMapper;
    }

    @Override
    public Flux<GetUserResponse> getUserByGen(Integer gen) {
        return getUserPort.findAllByDetail_GenOrderByNameAsc(gen)
                .subscribeOn(scheduler)
                .flatMap(userMapper::userToGetUserResponse);
    }

    @Override
    public Flux<GetUserResponse> getUserByMajor(String major) {
        return getUserPort.findAllByDetail_MajorOrderByNameAsc(getMajorType.execute(major))
                .subscribeOn(scheduler)
                .flatMap(userMapper::userToGetUserResponse);
    }

    @Override
    public Flux<GetUserResponse> getUserByClub(String club) {
        return getUserPort.findAllByDetail_ClubOrderByNameAsc(club)
                .subscribeOn(scheduler)
                .flatMap(userMapper::userToGetUserResponse);
    }

    @Override
    public Mono<GetUserResponse> getCurrentUser() {
        return getUserPort.currentUser()
                .flatMap(userMapper::userToGetUserResponse);
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
                .flatMap(userMapper::userToGetUserResponse);
    }

}
