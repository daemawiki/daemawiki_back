package org.daemawiki.domain.user.usecase;

import org.daemawiki.domain.user.dto.request.UpdateUserRequest;
import reactor.core.publisher.Mono;

public interface UpdateUserUsecase {
    Mono<Void> updateUser(UpdateUserRequest request);

}
