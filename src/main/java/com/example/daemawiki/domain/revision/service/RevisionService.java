package com.example.daemawiki.domain.revision.service;

import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class RevisionService {
    private final RevisionHistoryRepository revisionHistoryRepository;

    public RevisionService(RevisionHistoryRepository revisionHistoryRepository) {
        this.revisionHistoryRepository = revisionHistoryRepository;
    }

    public Flux<RevisionHistory> getUpdatedTop10Revision() {
        List<RevisionType> types = List.of(RevisionType.UPDATE, RevisionType.CREATE);
        return revisionHistoryRepository.findTop10ByTypeInOrderByUpdatedDateTimeDesc(types);
    }

    //        17077 92123
    //        17077 44486 최신
    public Flux<RevisionHistory> getAllRevisionPaging(String lastRevisionId) {
        return revisionHistoryRepository.findAllByOrderByUpdatedDateTimeDesc()
                .filter(revisionHistory -> lastRevisionId == null || lastRevisionId == "" ||
                        new ObjectId(revisionHistory.getId()).getTimestamp() > new ObjectId(lastRevisionId).getTimestamp())
                .take(10);
    }

}
