package com.example.daemawiki.domain.file.repository;

import com.example.daemawiki.domain.file.model.File;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface FileRepository extends ReactiveMongoRepository<File, UUID> {

    Flux<File> findByFileNameContaining(String fileName);

}
