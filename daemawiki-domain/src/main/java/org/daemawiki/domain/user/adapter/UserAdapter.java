package org.daemawiki.domain.user.adapter;

import org.daemawiki.domain.user.port.DeleteUserPort;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.port.SaveUserPort;
import org.daemawiki.domain.user.dto.FindUserDto;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.model.type.major.MajorType;
import org.daemawiki.domain.user.repository.UserRepository;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.daemawiki.utils.MongoQueryUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.Principal;
import java.util.Optional;

@Component
public class UserAdapter implements FindUserPort, SaveUserPort, DeleteUserPort {
    private final UserRepository userRepository;
    private final MongoQueryUtils mongoQueryUtils;

    public UserAdapter(UserRepository userRepository, MongoQueryUtils mongoQueryUtils) {
        this.userRepository = userRepository;
        this.mongoQueryUtils = mongoQueryUtils;
    }

    @Override
    public Mono<User> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(this::findByEmail);
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(UserNotFoundException.EXCEPTION)));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Flux<User> findAllByGenAndMajorAndClub(FindUserDto request) {
        Query query = buildQuery(request);
        query.with(PageRequest.of(request.pagingInfo().page(), request.pagingInfo().size()));

        return mongoQueryUtils.find(query, User.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Query buildQuery(FindUserDto request) {
        Query query = new Query();

        Optional.ofNullable(request.gen())
                .ifPresent(gen -> query.addCriteria(Criteria.where("detail.gen").is(gen)));

        if (isNotBlank(request.major())){
            query.addCriteria(Criteria.where("detail.major").is(MajorType.valueOf(request.major()).getMajor()));
        }

        if (isNotBlank(request.club())){
            query.addCriteria(Criteria.where("detail.club").is(request.club()));
        }

        Optional.ofNullable(request.pagingInfo().sortBy())
                .ifPresent(sortBy -> {
                    Sort.Direction direction = request.pagingInfo().sortDirection() == 1 ? Sort.Direction.ASC : Sort.Direction.DESC;
                    query.with(Sort.by(direction, sortBy));
                });

        return query;
    }

    private boolean isNotBlank(String str) {
        return !str.isBlank();
    }

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<Void> delete(User user) {
        return userRepository.delete(user);
    }

}
