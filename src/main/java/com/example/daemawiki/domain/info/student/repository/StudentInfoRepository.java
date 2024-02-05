package com.example.daemawiki.domain.info.student.repository;

import com.example.daemawiki.domain.info.student.model.StudentInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface StudentInfoRepository extends ReactiveMongoRepository<StudentInfo, String> {
    Flux<StudentInfo> findAllByGeneration(Integer generation);
    Flux<StudentInfo> findAllByName(String name);
    Flux<StudentInfo> findAllByMajor(String major);
}
