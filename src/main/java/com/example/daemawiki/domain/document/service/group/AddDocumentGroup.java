package com.example.daemawiki.domain.document.service.group;

import com.example.daemawiki.domain.document.dto.request.AddDocumentGroupRequest;
import com.example.daemawiki.domain.document.model.Group;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.user.service.UserFacade;
import com.example.daemawiki.global.dateTime.facade.DateTimeFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AddDocumentGroup {
    private final DocumentRepository documentRepository;
    private final DocumentFacade documentFacade;
    private final DateTimeFacade dateTimeFacade;
    private final UserFacade userFacade;

    public AddDocumentGroup(DocumentRepository documentRepository, DocumentFacade documentFacade, DateTimeFacade dateTimeFacade, UserFacade userFacade) {
        this.documentRepository = documentRepository;
        this.documentFacade = documentFacade;
        this.dateTimeFacade = dateTimeFacade;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(AddDocumentGroupRequest request) {
        return documentFacade.findDocumentById(request.documentId())
                .zipWith(dateTimeFacade.getKor(), (document, now) -> userFacade.currentUser()
                        .flatMap(user -> {
                            Group group = Group.builder()
                                    .root(request.groups().get(0))
                                    .classes(request.groups())
                                    .build();

                            document.getGroups()
                                    .add(group);

                            document.getDateTime().setUpdated(now);
                            document.getEditor().setUpdatedUser(user);

                            return documentRepository.save(document);
                        })).then();
    }

}
