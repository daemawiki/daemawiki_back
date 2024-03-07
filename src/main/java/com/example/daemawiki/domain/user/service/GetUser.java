package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.user.dto.response.GetUserResponse;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.model.mapper.UserMapper;
import com.example.daemawiki.domain.user.model.type.major.component.GetMajorType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetUser {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GetMajorType getMajorType;
    private final UserFacade userFacade;

    public GetUser(UserRepository userRepository, UserMapper userMapper, GetMajorType getMajorType, UserFacade userFacade, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.getMajorType = getMajorType;
        this.userFacade = userFacade;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Flux<GetUserResponse> getUserByGen(Integer gen) {
        return userRepository.findAllByDetail_GenOrderByNameAsc(gen)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Flux<GetUserResponse> getUserByMajor(String major) {
        return userRepository.findAllByDetail_MajorOrderByNameAsc(getMajorType.execute(major))
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Flux<GetUserResponse> getUserByClub(String club) {
        return userRepository.findAllByDetail_ClubOrderByNameAsc(club)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Mono<GetUserResponse> getCurrentUser() {
        return userFacade.currentUser()
                .flatMap(userMapper::userToGetUserResponse);
    }

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<GetUserResponse> getUserByGenAndMajorAndClub(Integer gen, String major, String club, String orderBy, String sort) {
        Query query = new Query();

        if(gen != 0) {
            query.addCriteria(Criteria.where("detail.gen").is(gen));
        }
        if(major != null && !major.isEmpty()) {
            query.addCriteria(Criteria.where("detail.major").is(getMajorType.execute(major.toLowerCase())));
        }
        if(club != null && !club.isEmpty()) {
            query.addCriteria(Criteria.where("detail.club").is(club));
        }
        if (sort != null && !sort.isEmpty() && (orderBy != null && !orderBy.isEmpty())) {
                if (sort.equalsIgnoreCase("desc")) {
                    query.with(Sort.by(Sort.Direction.DESC, orderBy));
                } else {
                    query.with(Sort.by(Sort.Direction.ASC, orderBy));
                }
        }

        return reactiveMongoTemplate.find(query, User.class)
                .flatMap(userMapper::userToGetUserResponse);
    }

}
