package org.daemawiki.domain.document_revision.adapter;

import org.daemawiki.domain.document_revision.port.SaveRevisionPort;
import org.daemawiki.domain.document_revision.port.FindRevisionPort;
import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.document_revision.repository.RevisionHistoryRepository;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RevisionAdapter implements SaveRevisionPort, FindRevisionPort {
    private final RevisionHistoryRepository revisionHistoryRepository;

    public RevisionAdapter(RevisionHistoryRepository revisionHistoryRepository) {
        this.revisionHistoryRepository = revisionHistoryRepository;
    }

    @Override
    public Mono<RevisionHistory> save(RevisionHistory revisionHistory) {
        return revisionHistoryRepository.save(revisionHistory);
    }

    @Override
    public Flux<RevisionHistory> findAllSortByUpdatedDate(PagingInfo pagingInfo, List<String> types) {
        List<RevisionType> typeValues = types.stream()
                        .map(type -> RevisionType.valueOf(type.toUpperCase()))
                        .toList();

        return revisionHistoryRepository.findAllByTypeIn(typeValues, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<RevisionHistory> findAll(PagingInfo pagingInfo) {
        return revisionHistoryRepository.findAllOrderByCustom(pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<RevisionHistory> findAllByDocumentId(String documentId, PagingInfo pagingInfo) {
        return revisionHistoryRepository.findAllByDocumentId(documentId, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<RevisionHistory> findAllByUserId(String userId, PagingInfo pagingInfo) {
        return revisionHistoryRepository.findAllByEditor_Id(userId, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

}
