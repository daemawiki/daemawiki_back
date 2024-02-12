package com.example.daemawiki.domain.revision.service;

import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class RevisionService {
    private final RevisionHistoryRepository revisionHistoryRepository;

    public RevisionService(RevisionHistoryRepository revisionHistoryRepository) {
        this.revisionHistoryRepository = revisionHistoryRepository;
    }

    public Flux<RevisionHistory> execute() {
        List<RevisionType> types = List.of(RevisionType.UPDATE, RevisionType.CREATE);
        return revisionHistoryRepository.findTop10ByTypeInOrderByUpdatedDateTimeDesc(types);
    }

}
