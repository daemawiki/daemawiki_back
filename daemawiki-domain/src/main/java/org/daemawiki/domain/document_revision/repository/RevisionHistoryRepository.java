package org.daemawiki.domain.document_revision.repository;

import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RevisionHistoryRepository extends ReactiveMongoRepository<RevisionHistory, String> {
    @Aggregation(pipeline = {
            "{ $match: { type: { $in: ?0 } } }",
            "{ $sort: { ?1: ?2 } }",
            "{ $group: { _id: '$documentId', document: { $first: '$$ROOT' } } }",
            "{ $replaceRoot: { newRoot: '$document' } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }",
    })
    Flux<RevisionHistory> findAllByTypeIn(List<RevisionType> types, String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $sort: { ?0: ?1 } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    Flux<RevisionHistory> findAllOrderByCustom(String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $match: { documentId: ?0 } }",
            "{ $sort: { ?1: ?2 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    Flux<RevisionHistory> findAllByDocumentId(String id, String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $match: { editor.id: ?0 } }",
            "{ $sort: { ?1: ?2 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    Flux<RevisionHistory> findAllByEditor_Id(String editorId, String sortBy, Integer sortDirection, Integer skip, Integer limit);

}
