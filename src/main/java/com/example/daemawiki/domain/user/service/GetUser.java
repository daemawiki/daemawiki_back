package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.user.dto.GetUserResponse;
import com.example.daemawiki.domain.user.model.mapper.UserMapper;
import com.example.daemawiki.domain.user.model.type.major.component.GetMajorType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetUser {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GetMajorType getMajorType;
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;

    public GetUser(UserRepository userRepository, UserMapper userMapper, GetMajorType getMajorType, UserFacade userFacade, DocumentFacade documentFacade) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.getMajorType = getMajorType;
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
    }

    public Flux<GetUserResponse> getUserByGen(Integer gen) {
        return userRepository.findAllByDetail_GenOrderByNameAsc(gen)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Flux<GetUserResponse> getUserByMajor(String major) {
        return userRepository.findAllByDetail_MajorOrderByNameAsc(getMajorType.execute(major))
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Mono<GetUserResponse> getCurrentUser() {
        return userFacade.currentUser()
                .flatMap(userMapper::userToGetUserResponse);
    }

}
