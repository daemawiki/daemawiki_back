package com.example.daemawiki.domain.info.student.service;

import com.example.daemawiki.domain.info.student.repository.StudentInfoRepository;
import com.example.daemawiki.domain.info.student.service.facade.StudentInfoFacade;
import com.example.daemawiki.domain.user.service.UserFacade;
import org.springframework.stereotype.Service;

@Service
public class DeleteStudentInfo {
    private final StudentInfoRepository studentInfoRepository;
    private final StudentInfoFacade studentInfoFacade;

    public DeleteStudentInfo(StudentInfoRepository studentInfoRepository, StudentInfoFacade studentInfoFacade) {
        this.studentInfoRepository = studentInfoRepository;
        this.studentInfoFacade = studentInfoFacade;
    }

}
