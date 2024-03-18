package org.daemawiki.domain.user.service;

import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.mapper.UserMapper;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.repository.UserRepository;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.domain.user.type.major.component.GetMajorType;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class GetUser {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GetMajorType getMajorType;
    private final UserFacade userFacade;
    private final Scheduler scheduler;

    public GetUser(UserRepository userRepository, UserMapper userMapper, GetMajorType getMajorType, UserFacade userFacade, ReactiveMongoTemplate reactiveMongoTemplate, Scheduler scheduler) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.getMajorType = getMajorType;
        this.userFacade = userFacade;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.scheduler = scheduler;
    }

    public Flux<GetUserResponse> getUserByGen(Integer gen) {
        return userRepository.findAllByDetail_GenOrderByNameAsc(gen)
                .subscribeOn(scheduler)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Flux<GetUserResponse> getUserByMajor(String major) {
        return userRepository.findAllByDetail_MajorOrderByNameAsc(getMajorType.execute(major))
                .subscribeOn(scheduler)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Flux<GetUserResponse> getUserByClub(String club) {
        return userRepository.findAllByDetail_ClubOrderByNameAsc(club)
                .subscribeOn(scheduler)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Mono<GetUserResponse> getCurrentUser() {
        return userFacade.currentUser()
                .flatMap(userMapper::userToGetUserResponse);
    }

    private final ReactiveMongoTemplate reactiveMongoTemplate;

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
