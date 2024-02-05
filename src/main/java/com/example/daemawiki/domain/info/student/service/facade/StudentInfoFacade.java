package com.example.daemawiki.domain.info.student.service.facade;

import com.example.daemawiki.domain.info.student.model.StudentInfo;
import com.example.daemawiki.domain.info.student.repository.StudentInfoRepository;
import com.example.daemawiki.global.exception.H404.StudentInfoNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StudentInfoFacade {
    private final StudentInfoRepository studentInfoRepository;

    public StudentInfoFacade(StudentInfoRepository studentInfoRepository) {
        this.studentInfoRepository = studentInfoRepository;
    }

    public Mono<StudentInfo> findStudentInfoById(String id) {
        return studentInfoRepository.findById(id)
                .switchIfEmpty(Mono.error(StudentInfoNotFoundException.EXCEPTION));
    }

}
