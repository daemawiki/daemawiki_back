package com.example.daemawiki.domain.mail.repository;

import com.example.daemawiki.domain.mail.model.AuthMail;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuthMailRepository extends ReactiveCrudRepository<AuthMail, String> {
}
