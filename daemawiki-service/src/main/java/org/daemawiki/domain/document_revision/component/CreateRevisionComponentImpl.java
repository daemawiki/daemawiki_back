package org.daemawiki.domain.document_revision.component;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document_revision.dto.request.SaveRevisionHistoryDto;
import org.daemawiki.domain.document_revision.model.RevisionDetail;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.document_revision.usecase.CreateRevisionUsecase;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CreateRevisionComponentImpl implements CreateRevisionComponent {
    private final CreateRevisionUsecase createRevisionUsecase;

    public CreateRevisionComponentImpl(CreateRevisionUsecase createRevisionUsecase) {
        this.createRevisionUsecase = createRevisionUsecase;
    }

    @Override
    public Mono<Void> create(DefaultDocument document, RevisionType revisionType, List<RevisionDetail> data) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryDto
                .create(revisionType, document.getId(), document.getVersion(), document.getTitle(), data));
    }

}
