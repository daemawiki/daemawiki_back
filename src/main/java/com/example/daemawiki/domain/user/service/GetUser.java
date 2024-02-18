package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.user.dto.GetUserResponse;
import com.example.daemawiki.domain.user.model.mapper.UserMapper;
import com.example.daemawiki.domain.user.model.type.component.GetMajorType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetUser {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GetMajorType getMajorType;

    public GetUser(UserRepository userRepository, UserMapper userMapper, GetMajorType getMajorType) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.getMajorType = getMajorType;
    }

    public Flux<GetUserResponse> getUserByGen(Integer gen) {
        return userRepository.findAllByGenOrderByNameAsc(gen)
                .flatMap(userMapper::userToGetUserResponse);
    }

    public Flux<GetUserResponse> getUserByMajor(String major) {
        return userRepository.findAllByMajorOrderByNameAsc(getMajorType.execute(major))
                .flatMap(userMapper::userToGetUserResponse);
    }

}
