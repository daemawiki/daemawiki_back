package org.daemawiki.domain.admin.usecase;

import org.daemawiki.domain.admin.dto.GetAdminResponse;
import reactor.core.publisher.Flux;

public interface GetAdminUsecase {
    Flux<GetAdminResponse> getAll();

}
