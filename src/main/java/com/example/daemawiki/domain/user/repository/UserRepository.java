package com.example.daemawiki.domain.user.repository;

import com.example.daemawiki.domain.user.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
