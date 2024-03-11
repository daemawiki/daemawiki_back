package com.example.daemawiki.domain.revision.component;

import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import reactor.core.publisher.Mono;

public interface RevisionComponent {
    Mono<Void> saveHistory(SaveRevisionHistoryRequest request);

}
