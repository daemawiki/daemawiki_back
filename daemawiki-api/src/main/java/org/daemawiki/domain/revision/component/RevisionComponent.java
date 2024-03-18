package org.daemawiki.domain.revision.component;

import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.exception.h500.ExecuteFailedException;
import reactor.core.publisher.Mono;

public interface RevisionComponent {
    /**
     * @author 김승원
     * @param saveRevisionHistoryRequest
     * @return void
     * @exception ExecuteFailedException
     */
    Mono<Void> saveHistory(SaveRevisionHistoryRequest saveRevisionHistoryRequest);

}
