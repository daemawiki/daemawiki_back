package org.daemawiki.domain.document.component.facade;

import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Mono;

public interface CreateDocumentFacade {
    Mono<DefaultDocument> create(SaveDocumentRequest request, User user);

}
