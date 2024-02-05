package com.example.daemawiki.domain.info.student.service;

import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.info.student.dto.CreateStudentInfoRequest;
import com.example.daemawiki.domain.info.student.model.PhysicalInfo;
import com.example.daemawiki.domain.info.student.model.StudentInfo;
import com.example.daemawiki.domain.info.student.repository.StudentInfoRepository;
import com.example.daemawiki.domain.user.service.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateStudentInfo {
    private final StudentInfoRepository studentInfoRepository;
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;

    public CreateStudentInfo(StudentInfoRepository studentInfoRepository, DocumentFacade documentFacade, UserFacade userFacade) {
        this.studentInfoRepository = studentInfoRepository;
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(CreateStudentInfoRequest request) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(request.documentId()), (user, document) -> StudentInfo.builder()
                            .name(request.name())
                            .age(request.age())
                            .generation(request.generation())
                            .gender(request.gender())
                            .physicalInfo(PhysicalInfo.builder()
                                    .height(request.height())
                                    .weight(request.weight())
                                    .build())
                            .birthday(request.birthday())
                            .major(request.major())
                            .nickname(request.nickname())
                            .document(document)
                            .build())
                .flatMap(studentInfoRepository::save).then();
    }

}
