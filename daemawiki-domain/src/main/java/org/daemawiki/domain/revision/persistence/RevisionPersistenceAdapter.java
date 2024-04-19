package org.daemawiki.domain.revision.persistence;

import org.daemawiki.domain.revision.application.SaveRevisionPort;
import org.daemawiki.domain.revision.application.FindRevisionPort;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RevisionPersistenceAdapter implements SaveRevisionPort, FindRevisionPort {
    private final RevisionHistoryRepository revisionHistoryRepository;

    public RevisionPersistenceAdapter(RevisionHistoryRepository revisionHistoryRepository) {
        this.revisionHistoryRepository = revisionHistoryRepository;
    }

    @Override
    public Mono<RevisionHistory> save(RevisionHistory revisionHistory) {
        return revisionHistoryRepository.save(revisionHistory);
    }

    @Override
    public Flux<RevisionHistory> getRevisionOrderByUpdated(PagingInfo pagingInfo, List<String> types) {
        List<RevisionType> typeValues = types.stream()
                        .map(type -> RevisionType.valueOf(type.toUpperCase()))
                        .toList();

        return revisionHistoryRepository.findAllByTypeIn(typeValues, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionPaging(PagingInfo pagingInfo) {
        return revisionHistoryRepository.findAllOrderByCustom(pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, PagingInfo pagingInfo) {
        return revisionHistoryRepository.findAllByDocumentId(documentId, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByUser(String userId, PagingInfo pagingInfo) {
        return revisionHistoryRepository.findAllByEditor_Id(userId, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

}
