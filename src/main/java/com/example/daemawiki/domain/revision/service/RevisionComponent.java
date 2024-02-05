package com.example.daemawiki.domain.revision.service;

import com.example.daemawiki.domain.revision.dto.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import com.example.daemawiki.domain.user.service.UserFacade;
import com.example.daemawiki.global.dateTime.facade.DateTimeFacade;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RevisionComponent {
    private final RevisionHistoryRepository revisionHistoryRepository;
    private final UserFacade userFacade;
    private final DateTimeFacade dateTimeFacade;

    public RevisionComponent(RevisionHistoryRepository revisionHistoryRepository, UserFacade userFacade, DateTimeFacade dateTimeFacade) {
        this.revisionHistoryRepository = revisionHistoryRepository;
        this.userFacade = userFacade;
        this.dateTimeFacade = dateTimeFacade;
    }

    public Mono<Void> saveHistory(SaveRevisionHistoryRequest request) {
        return userFacade.currentUser()
                .zipWith(dateTimeFacade.getKor(), (user, now) -> RevisionHistory.builder()
                        .type(request.type())
                        .documentId(request.documentId())
                        .title(request.title())
                        .editor(user.getId())
                        .updatedDateTime(now)
                        .build())
                .flatMap(revisionHistoryRepository::save)
                .then();
    }

}
