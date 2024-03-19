package org.daemawiki.domain.document.usecase;

import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Mono;

public interface CreateDocumentUsecase {
    Mono<Void> create(SaveDocumentRequest request);
    Mono<DefaultDocument> createByUser(User user);
}
