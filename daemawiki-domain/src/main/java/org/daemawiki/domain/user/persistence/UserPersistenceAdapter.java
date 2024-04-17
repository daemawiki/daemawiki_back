package org.daemawiki.domain.user.persistence;

import org.daemawiki.domain.user.application.DeleteUserPort;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.model.type.major.MajorType;
import org.daemawiki.domain.user.repository.UserRepository;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
public class UserPersistenceAdapter implements FindUserPort, SaveUserPort, DeleteUserPort {
    private final UserRepository userRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public UserPersistenceAdapter(UserRepository userRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.userRepository = userRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
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
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Flux<User> findAllByDetail_GenOrderByNameAsc(Integer gen) {
        return userRepository.findAllByDetail_GenOrderByNameAsc(gen);
    }

    @Override
    public Flux<User> findAllByDetail_MajorOrderByNameAsc(MajorType major) {
        return userRepository.findAllByDetail_MajorOrderByNameAsc(major);
    }

    @Override
    public Flux<User> findAllByDetail_ClubOrderByNameAsc(String club) {
        return userRepository.findAllByDetail_ClubOrderByNameAsc(club);
    }

    @Override
    public Flux<User> findAllByGenAndMajorAndClub(Integer gen, String major, String club, String orderBy, String sort) {
        Query query = new Query();

        if(gen != null) {
            query.addCriteria(Criteria.where("detail.gen").is(gen));
        }
        if(major != null && !major.isBlank()) {
            query.addCriteria(Criteria.where("detail.major").is(MajorType.valueOf(major).getMajor()));
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

        return reactiveMongoTemplate.find(query, User.class);
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
